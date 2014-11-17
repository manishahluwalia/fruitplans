
package fruit.health.server.cloner.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.inject.Singleton;

import fruit.health.server.cloner.api.Clone;
import fruit.health.server.cloner.api.CloningError;
import fruit.health.server.cloner.api.CopyFromClient;
import fruit.health.server.cloner.api.DoNotClone;
import fruit.health.server.cloner.api.Projection;
import fruit.health.server.cloner.api.ReflexivelyClonable;
import fruit.health.server.util.Utils;

/**
 * Provides a bunch of actions on "Clonable" types.
 * <p/>
 * A "Clonable" type must meet the following requirements:
 * <ol>
 * <li>It must have a publicly accessible no-argument constructor</li>
 * <li>It must follow standard conventions for getters and setters on all
 * clonable fields</li>
 * <li>It must be one of the following types:
 * <ol>
 * <li>A primitive type (e.g. int), its Object counterpart (e.g. Integer),
 * {@link String} or an {@link Enum}. These type are called "Immutable"</li>
 * <li>A {@link Collection} of clonable types</li>
 * <li>An array of clonable types</li>
 * <li>A class annotated with {@link ReflexivelyClonable}. For such types, only
 * fields (including inherited fields) explicitly annotated with {@link Clone}
 * or {@link CopyFromClient} are considered, and all of them must be clonable.
 * Circular references are fine. All fields must have a public accessible, no
 * argument getter method following Java conventions.
 * </ol>
 * </ol>
 * <p/>
 * This class provides the following services on Clonable types:
 * <ol>
 * <li>Making a deep clone of the object (see {@link #deepClone(Object)})</li>
 * <li>Inspecting the object to check if it is suitable for GWT serialization as
 * a response to a GWT-RPC call (see {@link #copyForGwtRpcIfNeeded(Object)})</li>
 * <li>Updating the server's copy of an object with selected fields from a copy
 * of the object that was just transmitted by the client (see
 * {@link #shallowCopyFieldsFromClient(Object, Object)})</li>
 * <li>Performing the above under constraints of some projections (see below,
 * and {@link #deepClone(Object, Class)},
 * {@link #copyForGwtRpcIfNeeded(Object, Class)} and
 * {@link #shallowCopyFieldsFromClient(Object, Object)})</li>
 * </ol>
 * <p/>
 * A field to be considered for cloning on the server can be subjected to
 * projection. For instance, suppose you have an Employee object which has a
 * whole set of fields. Some fields (like the employee's address) need to be
 * visible only to the employee. Other fields (like the title) need to be
 * visible to everyone. Other fields (like salary) should be visible to the
 * employee and the manager. When attempting to return the Employee object as
 * the result of a GWT-RPC call, the server can determine which projection is
 * appropriate and apply it to the employee object. Only fields that are
 * declared to be clonable under the desired projection will be visible to the
 * user; those declared to be not visible are guaranteed to be set to their
 * default values; those not declared to be either will be treated by the code
 * in whichever manner is most performance efficient. See the {@link Clone} and
 * {@link DoNotClone} annotations and {@link Projection} for some more details.
 * Here is an example of the meta-data for the employee class:
 * <p/>
 * 
 * <pre>
 * interface EmployeeProjection extends Projection
 * {
 * }
 * 
 * interface ManagerProjection extends Projection
 * {
 * }
 * 
 * class Employee
 * {
 *     &#064;Clone String title; // Everyone sees it
 * 
 *     &#064;Clone(EmployeeProjection.class) @DoNotClone String address; // Only
 *                                                                  // employee
 *                                                                  // sees it
 * 
 *     &#064;Clone({ EmployeeProjection.class, ManagerProjection.class }) @DoNotClone int salary; // emp
 *                                                                                           // +
 *                                                                                           // mgr
 *                                                                                           // see
 *                                                                                           // it
 * 
 *     &#064;DoNotClone String passwordHash; // Stays on server; all clients see it as
 *                                      // null
 * }
 * </pre>
 * <p/>
 * Here are a set of rules demonstrating when a field is Cloned, Set to default
 * value, or treated indeterminately (whatever is the most efficient for the
 * server). Consider the following:
 * 
 * <pre>
 * interface ProjectionA extends Projection
 * {
 * }
 * 
 * interface ProjectionB extends Projection
 * {
 * }
 * 
 * interface ProjectionC extends Projection
 * {
 * }
 * 
 * class CloningDispositionTest
 * {
 *     public Integer noAnnotations;
 * 
 *     &#064;Clone public Integer cloned;
 * 
 *     &#064;DoNotClone public Integer notCloned;
 * 
 *     &#064;Clone(ProjectionA.class) public Integer clonedOnA;
 * 
 *     &#064;DoNotClone(ProjectionA.class) public Integer notClonedOnA;
 * 
 *     &#064;Clone(ProjectionA.class) @DoNotClone public Integer clonedOnAOnly;
 * 
 *     &#064;DoNotClone(ProjectionA.class) @Clone public Integer notClonedOnAOnly;
 * 
 *     &#064;Clone(ProjectionA.class) @DoNotClone(ProjectionB.class) public Integer clonedOnAButNotB;
 * }
 * </pre>
 * 
 * Legend:
 * <ul>
 * <li>C: Cloned. Client sees the value on the server</li>
 * <li>X: Not cloned. Client sees default value for type</li>
 * <li>-: Server will do whatever is most efficient</li>
 * </ul>
 * <table border="1">
 * <tr>
 * <th>Field</th>
 * <th>No projection used</th>
 * <th>ProjectionA</th>
 * <th>ProjectionB</th>
 * <th>ProjectionC</th>
 * </tr>
 * <tr>
 * <td>noAnnotations</td>
 * <td>-</td>
 * <td>-</td>
 * <td>-</td>
 * <td>-</td>
 * </tr>
 * <tr>
 * <td>cloned</td>
 * <td>C</td>
 * <td>C</td>
 * <td>C</td>
 * <td>C</td>
 * </tr>
 * <tr>
 * <td>notCloned</td>
 * <td>X</td>
 * <td>X</td>
 * <td>X</td>
 * <td>X</td>
 * </tr>
 * <tr>
 * <td>clonedOnA</td>
 * <td>C</td>
 * <td>C</td>
 * <td>-</td>
 * <td>-</td>
 * </tr>
 * <tr>
 * <td>notClonedOnA</td>
 * <td>X</td>
 * <td>X</td>
 * <td>-</td>
 * <td>-</td>
 * </tr>
 * <tr>
 * <td>clonedOnAButNotB</td>
 * <td>-</td>
 * <td>C</td>
 * <td>X</td>
 * <td>-</td>
 * </tr>
 * <tr>
 * <td>notClonedOnAOnly</td>
 * <td>C</td>
 * <td>X</td>
 * <td>C</td>
 * <td>C</td>
 * </tr>
 * <tr>
 * <td>clonedOnAOnly</td>
 * <td>X</td>
 * <td>C</td>
 * <td>X</td>
 * <td>X</td>
 * </tr>
 * </table>
 * The "cloned", "notCloned" and "clonedOnAOnly" are probably the most useful
 * patterns to keep in mind.
 * <p/>
 * See: {@link ReflexivelyClonable} {@link Clone} {@link DoNotClone}
 * {@link CopyFromClient}
 */
@Singleton
public class RecursiveReflexiveCloner
{
    public class FieldAccessorClonerWrapper
    {
        private final Field field;

        /*
         * The following 2 fields are a cache. We look at the type of the field
         * and get a cloner for it, if it makes sense.
         */
        private Cloner cloner;
        private Class<?> clonerForType;

        private final HashSet<Class<? extends Projection>> cloneProjections;
        private final HashSet<Class<? extends Projection>> doNotCloneProjections;
        private final HashSet<Class<? extends Projection>> copyFromClientProjections;

        private final Method getter;
        private final Method setter;

        public FieldAccessorClonerWrapper (Field field, Clone clone,
                DoNotClone doNotClone, CopyFromClient copyFromClient)
        {
            if (null != clone)
            {
                cloneProjections = new HashSet<Class<? extends Projection>>(Arrays.asList(clone.value()));
            }
            else
            {
                cloneProjections = null;
            }
            if (null != doNotClone)
            {
                doNotCloneProjections = new HashSet<Class<? extends Projection>>(Arrays.asList(doNotClone.value()));
            }
            else
            {
                doNotCloneProjections = null;
            }
            if (null != cloneProjections && null != doNotCloneProjections)
            {
                Set<Class<? extends Projection>> intersection = new HashSet<Class<? extends Projection>>(cloneProjections);
                intersection.retainAll(doNotCloneProjections);
                if (intersection.size() > 0)
                {
                    throw new CloningError(field.getName()
                            + " is both @Cloned and @DoNotCloned for projection "
                            + intersection.iterator().next().getName());
                }
            }
            if (null != cloneProjections && cloneProjections.isEmpty()
                    && null != doNotCloneProjections
                    && doNotCloneProjections.isEmpty())
            {
                throw new CloningError(field.getName()
                        + " is annotated both @Clone and @DoNotClone for all projections");
            }

            if (null != copyFromClient)
            {
                copyFromClientProjections = new HashSet<Class<? extends Projection>>(Arrays.asList(copyFromClient.value()));
            }
            else
            {
                copyFromClientProjections = null;
            }

            field.setAccessible(true);
            this.field = field;
            try
            {
                this.getter = Utils.convertToGetterMethod(field);
            }
            catch (Exception e)
            {
                throw new CloningError("Can't get getter method for "
                        + field.getName(), e);
            }
            try
            {
                this.setter = Utils.convertToSetterMethod(field);
            }
            catch (Exception e)
            {
                throw new CloningError("Can't get setter method for "
                        + field.getName(), e);
            }

            Class<?> fieldType = field.getType();
            if (0 != (field.getModifiers() & (Modifier.INTERFACE | Modifier.ABSTRACT)))
            {
                /*
                 * The field is either an interface or an abstract class. There
                 * will never be an object of this very type, so no need to
                 * precompute the cloner here. We have no choice but to try and
                 * compute the cloner type when we see a concrete object.
                 */
                this.cloner = null;
                this.clonerForType = null;
            }
            else
            {
                try
                {
                    this.cloner = getClonerForType(fieldType);
                    this.clonerForType = fieldType;
                }
                catch (CloningError e)
                {
                    /*
                     * Can't get cloner for this type. Perhaps it will never be
                     * instantiated incorrectly. Perhaps it will. Defer cloner
                     * computation (and thus, checking) to actual use.
                     */
                    this.cloner = null;
                    this.clonerForType = null;
                }
            }
        }

        public CloningDisposition getCloningDisposition (
                Class<? extends Projection> projection)
        {
            if (null == cloneProjections)
            {
                if (null != doNotCloneProjections
                        && (null == projection
                                || doNotCloneProjections.isEmpty() || doNotCloneProjections.contains(projection)))
                {
                    return CloningDisposition.DONT_CLONE;
                }

                return CloningDisposition.DONT_CARE;
            }
            if (null == doNotCloneProjections)
            {
                if (null == projection || cloneProjections.isEmpty()
                        || cloneProjections.contains(projection))
                {
                    return CloningDisposition.CLONE;
                }

                return CloningDisposition.DONT_CARE;
            }
            if (doNotCloneProjections.contains(projection))
            {
                return CloningDisposition.DONT_CLONE;
            }
            if (cloneProjections.contains(projection))
            {
                return CloningDisposition.CLONE;
            }
            if (cloneProjections.isEmpty())
            {
                return CloningDisposition.CLONE;
            }
            if (doNotCloneProjections.isEmpty())
            {
                return CloningDisposition.DONT_CLONE;
            }
            return CloningDisposition.DONT_CARE;
        }

        public boolean cloneNeededForGwt (Object object,
                Class<? extends Projection> projection,
                Set<Object> alreadyChecked)
        {
            Object src;
            try
            {
                src = getter.invoke(object);
            }
            catch (Exception e)
            {
                throw new CloningError("Can't invoke method "
                        + getter.getName(), e);
            }
            if (null == src)
            {
                return false;
            }
            if (CloningDisposition.DONT_CLONE == getCloningDisposition(projection))
            {
                /*
                 * If this is not supposed to be cloned, and the field is
                 * non-null, then we must deepClone it so that GWT-RPC does not
                 * pass this on to the client.
                 */
                return true;
            }
            return getCloner(src).cloneNeededForGwt(src,
                    projection,
                    alreadyChecked);
        }

        private Cloner getCloner (Object obj)
        {
            Cloner cloner = this.cloner;
            if (null == cloner || !obj.getClass().equals(clonerForType))
            {
                try
                {
                    cloner = getClonerForType(obj.getClass());
                }
                catch (Exception e)
                {
                    throw new CloningError("Can't get cloner for object of type "
                            + obj.getClass().getName()
                            + " assigned to field "
                            + field.getName(),
                            e);
                }
            }
            return cloner;
        }

        public void clone (Object destination, Object source,
                Class<? extends Projection> projection,
                Map<Object, Object> alreadyXlated)
        {
            Object src;
            try
            {
                src = getter.invoke(source);
            }
            catch (Exception e)
            {
                throw new CloningError("Can't invoke method "
                        + getter.getName(), e);
            }
            Object newObject = null;
            if (null != src)
            {
                Cloner cloner = getCloner(src);
                try
                {
                    newObject = cloner.deepClone(src, projection, alreadyXlated);
                }
                catch (Exception e)
                {
                    throw new CloningError("Can't clone field "
                            + field.getName(), e);
                }
            }
            if (CloningDisposition.CLONE == getCloningDisposition(projection))
            {
                try
                {
                    setter.invoke(destination, newObject);
                }
                catch (Exception e)
                {
                    throw new CloningError("Can't invoke method "
                            + setter.getName(), e);
                }
            }
        }

        public void shallowCopyField (Object destination, Object source,
                Class<? extends Projection> projection)
        {
            if (null == copyFromClientProjections)
            {
                return;
            }

            if (copyFromClientProjections.isEmpty()
                    || copyFromClientProjections.contains(projection))
            {
                Object srcValue;
                try
                {
                    srcValue = getter.invoke(source);
                }
                catch (Exception e)
                {
                    throw new CloningError("Can't invoke method "
                            + getter.getName(), e);
                }
                try
                {
                    setter.invoke(destination, srcValue);
                }
                catch (Exception e)
                {
                    throw new CloningError("Can't invoke method "
                            + setter.getName(), e);
                }
            }
        }
    }

    /**
     * A cloner that handles any {@link ReflexivelyClonable}.
     * <p/>
     * Must be constructed in 2 phases:
     * <nl>
     * <li>Call the constructor</li>
     * <li>call initialize()</li>
     * </nl>
     */
    private class ReflexiveCloner extends Cloner
    {
        private List<FieldAccessorClonerWrapper> cloningAccessors;
        private List<FieldAccessorClonerWrapper> copyingAccessors;
        private final Class<?> type;

        public ReflexiveCloner (Class<?> type)
        {
            this.type = type;
        }

        public void initialize ()
        {
            cloningAccessors = new LinkedList<RecursiveReflexiveCloner.FieldAccessorClonerWrapper>();
            copyingAccessors = new LinkedList<RecursiveReflexiveCloner.FieldAccessorClonerWrapper>();

            Class<?> t = this.type;
            do
            {
                for (Field field : t.getDeclaredFields())
                {
                    Clone clone = field.getAnnotation(Clone.class);
                    DoNotClone doNotClone = field.getAnnotation(DoNotClone.class);
                    CopyFromClient copyFromClient = field.getAnnotation(CopyFromClient.class);

                    if (null == clone && null == doNotClone
                            && null == copyFromClient)
                    {
                        continue;
                    }
                    if ((field.getModifiers() & Modifier.STATIC) != 0)
                    {
                        throw new CloningError("static field "
                                + field.getName()
                                + " cannot be annotated for cloning");
                    }
                    if ((field.getModifiers() & Modifier.FINAL) != 0)
                    {
                        throw new CloningError("final field " + field.getName()
                                + " cannot be annotated for cloning");
                    }

                    FieldAccessorClonerWrapper wrapper;
                    try
                    {
                        wrapper = new FieldAccessorClonerWrapper(field,
                                clone,
                                doNotClone,
                                copyFromClient);
                    }
                    catch (Exception e)
                    {
                        throw new CloningError(field.getName() + " of type "
                                + field.getType().getName() + " in "
                                + t.getName() + " cannot be cloned", e);
                    }

                    if (null != clone || null != doNotClone)
                    {
                        cloningAccessors.add(wrapper);
                    }
                    if (null != copyFromClient)
                    {
                        copyingAccessors.add(wrapper);
                    }
                }

                t = t.getSuperclass();

            } while (null != t);
        }

        @Override
        public boolean cloneNeededForGwt (Object object,
                Class<? extends Projection> projection,
                Set<Object> alreadyChecked)
        {
            if (!alreadyChecked.add(object))
            {
                return false;
            }

            for (FieldAccessorClonerWrapper accessor : cloningAccessors)
            {
                try
                {
                    if (accessor.cloneNeededForGwt(object,
                            projection,
                            alreadyChecked))
                    {
                        return true;
                    }
                }
                catch (Exception e)
                {
                    throw new CloningError("Can't recursively evaluate "
                            + object.getClass().getName(), e);
                }
            }
            return false;
        }

        @Override
        public Object deepClone (Object source,
                Class<? extends Projection> projection,
                Map<Object, Object> alreadyXlated)
        {
            if (alreadyXlated.containsKey(source))
            {
                return alreadyXlated.get(source);
            }

            Object destination;
            try
            {
                destination = source.getClass().newInstance();
                alreadyXlated.put(source, destination);
            }
            catch (Exception e)
            {
                throw new CloningError("Can't create object of type "
                        + source.getClass().getName()
                        + ". Did you forget to declare a public no-argument constructor?",
                        e);
            }

            for (FieldAccessorClonerWrapper accessor : cloningAccessors)
            {
                try
                {
                    accessor.clone(destination,
                            source,
                            projection,
                            alreadyXlated);
                }
                catch (Exception e)
                {
                    throw new CloningError("Can't recursively clone "
                            + source.getClass().getName(), e);
                }
            }
            return destination;
        }

        @Override
        public void copyFromClient (Object serverDestinationObject,
                Object clientSourceObject,
                Class<? extends Projection> projection)
        {
            for (FieldAccessorClonerWrapper accessor : copyingAccessors)
            {
                try
                {
                    accessor.shallowCopyField(serverDestinationObject,
                            clientSourceObject,
                            projection);
                }
                catch (Exception e)
                {
                    throw new CloningError("Can't recursively clone "
                            + serverDestinationObject.getClass().getName(), e);
                }
            }
        }
    }

    private class ArrayCloner extends Cloner
    {
        protected Class<?> clazz;

        public ArrayCloner (Class<?> componentType)
        {
            this.clazz = componentType;
        }

        @Override
        public boolean cloneNeededForGwt (Object sourceArray,
                Class<? extends Projection> projection,
                Set<Object> alreadyChecked)
        {
            if (!alreadyChecked.add(sourceArray))
            {
                return false;
            }

            int len = Array.getLength(sourceArray);
            for (int i = 0; i < len; i++)
            {
                try
                {
                    Object item = Array.get(sourceArray, i);
                    if (null != item
                            && getClonerForType(item.getClass()).cloneNeededForGwt(item,
                                    projection,
                                    alreadyChecked))
                    {
                        return true;
                    }
                }
                catch (Exception e)
                {
                    throw new CloningError("Error at index " + i, e);
                }
            }
            return false;
        }

        @Override
        public Object deepClone (Object sourceArray,
                Class<? extends Projection> projection,
                Map<Object, Object> alreadyXlated)
        {
            if (alreadyXlated.containsKey(sourceArray))
            {
                return alreadyXlated.get(sourceArray);
            }

            int len = Array.getLength(sourceArray);
            Object newArray = Array.newInstance(clazz, len);
            alreadyXlated.put(sourceArray, newArray);
            for (int i = 0; i < len; i++)
            {
                try
                {
                    Object item = Array.get(sourceArray, i);
                    if (null == item)
                    {
                        Array.set(newArray, i, null);
                    }
                    else
                    {
                        Cloner cloner = getClonerForType(item.getClass());
                        Array.set(newArray, i, cloner.deepClone(item,
                                projection,
                                alreadyXlated));
                    }
                }
                catch (Exception e)
                {
                    throw new CloningError("Error at index " + i, e);
                }
            }
            return newArray;
        }
    }

    private class JavaUtilCollectionCloner extends Cloner
    {
        @Override
        public boolean cloneNeededForGwt (Object object,
                Class<? extends Projection> projection,
                Set<Object> alreadyChecked)
        {
            if (!alreadyChecked.add(object))
            {
                return false;
            }

            Class<?> clazz = object.getClass();
            Class<?> maximalClass = getMaximalClass(clazz);
            if (null == maximalClass)
            {
                throw new CloningError(clazz.getName() + " not yet implemented");
            }
            else if (!maximalClass.equals(clazz))
            {
                return true;
            }
            else
            {
                for (Object o : (Collection<?>)object)
                {
                    if (null != o
                            && getClonerForType(o.getClass()).cloneNeededForGwt(o,
                                    projection,
                                    alreadyChecked))
                    {
                        return true;
                    }
                }
                return false;
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object deepClone (Object source,
                Class<? extends Projection> projection,
                Map<Object, Object> alreadyXlated)
        {
            if (alreadyXlated.containsKey(source))
            {
                return alreadyXlated.get(source);
            }

            @SuppressWarnings("rawtypes")
            Class<? extends Collection> clazz = getMaximalClass(source.getClass());
            if (null == clazz)
            {
                throw new CloningError(source.getClass().getName()
                        + " not yet implemented");
            }
            Collection<Object> newObject;
            try
            {
                newObject = clazz.newInstance();
                alreadyXlated.put(source, newObject);
            }
            catch (Exception e)
            {
                throw new CloningError("Can't instantiate " + clazz.getName(),
                        e);
            }
            for (Object o : (Collection<?>)source)
            {
                if (null == o)
                {
                    newObject.add(null);
                }
                else
                {
                    newObject.add(getClonerForType(o.getClass()).deepClone(o,
                            projection,
                            alreadyXlated));
                }
            }
            return newObject;
        }

        @SuppressWarnings("rawtypes")
        private Class<? extends Collection> getMaximalClass (Class<?> clazz)
        {
            // TODO add more cases
            if (HashSet.class.isAssignableFrom(clazz))
            {
                return (Class<? extends Collection>)HashSet.class;
            }
            if (LinkedList.class.isAssignableFrom(clazz))
            {
                return (Class<? extends Collection>)LinkedList.class;
            }
            if (TreeSet.class.isAssignableFrom(clazz))
            {
                return (Class<? extends Collection>)TreeSet.class;
            }
            return null;
        }
    }

    private class SimpleArrayCloner extends ArrayCloner
    {
        public SimpleArrayCloner (Class<?> componentType, Cloner clonerForType)
        {
            super(componentType);
        }

        @Override
        public boolean cloneNeededForGwt (Object object,
                Class<? extends Projection> projection,
                Set<Object> alreadyChecked)
        {
            return false;
        }
    }

    private class ImmutableTypeCloner extends Cloner
    {
        @Override
        public boolean cloneNeededForGwt (Object object,
                Class<? extends Projection> projection,
                Set<Object> alreadyChecked)
        {
            return false;
        }

        @Override
        public Object deepClone (Object source,
                Class<? extends Projection> projection,
                Map<Object, Object> alreadyXlated)
        {
            return source;
        }
    }

    private class DateCloner extends Cloner
    {
        @Override
        public boolean cloneNeededForGwt (Object object,
                Class<? extends Projection> projection,
                Set<Object> alreadyChecked)
        {
            return false;
        }

        @Override
        public Object deepClone (Object source,
                Class<? extends Projection> projection,
                Map<Object, Object> alreadyXlated)
        {
            return new Date(((Date)source).getTime());
        }
    }

    private final HashSet<Class<?>> immutableTypes;

    private ImmutableTypeCloner immutableTypeCloner = new ImmutableTypeCloner();
    private DateCloner dateCloner = new DateCloner();
    private JavaUtilCollectionCloner javaUtilCollectionCloner = new JavaUtilCollectionCloner();

    private volatile HashMap<Class<?>, Cloner> cloningActions = new HashMap<Class<?>, Cloner>();

    public RecursiveReflexiveCloner ()
    {
        this(null);
    }

    public RecursiveReflexiveCloner (HashSet<Class<?>> immutableTypes)
    {
        if (null == immutableTypes)
        {
            this.immutableTypes = new HashSet<Class<?>>();
        }
        else
        {
            this.immutableTypes = new HashSet<Class<?>>(immutableTypes);
        }
        this.immutableTypes.add(String.class);
        this.immutableTypes.add(Byte.class);
        this.immutableTypes.add(Character.class);
        this.immutableTypes.add(Short.class);
        this.immutableTypes.add(Integer.class);
        this.immutableTypes.add(Long.class);
        this.immutableTypes.add(Float.class);
        this.immutableTypes.add(Double.class);
        this.immutableTypes.add(Boolean.class);
    }

    /**
     * Like {@link #copyForGwtRpcIfNeeded(Object, Class)}, with a null
     * projection.
     */
    public <T> T copyForGwtRpcIfNeeded (T source)
    {
        return copyForGwtRpcIfNeeded(source, null);
    }

    /**
     * Given an object, makes a clone of it in preparation for serialization by
     * GWT as response to an RPC call. This method uses reflection to walk the
     * object's members and clone them recursively if needed. The original
     * object graph is never modified.
     * <p/>
     * This function can make a deep clone if needed. However, in some cases, we
     * know that the original object is good enough. For instance, since the
     * object is going to be returned as the response of a GWT-RPC call, a deep
     * clone might be wasteful since GWT-RPC is going to read the object (no
     * writes) and serialize it anyway. In this case, we do want some data
     * structures simplified. For instance, some ORMs will replace every
     * {@link LinkedList} with a subclasses that loads items for the database
     * lazily. GWT-RPC can serialize a {@link LinkedList}, but not its
     * subclasses. This routine will clone these sub-classed {@link LinkedList}s
     * into {@link LinkedList}s, for which, it will have to make a deep clone,
     * since the original objects cannot be modified as per the API definition
     * of this function.
     * <p/>
     * A clone for GWT-RPC may or may not actually copy items over. i.e. it may
     * be that:
     * 
     * <pre>
     * Object orig;
     * Object clone = deepClone(orig);
     * clone==deepClone(orig)
     * </pre>
     * 
     * is false, although it generally will be true.
     * <p/>
     * Note that only fields marked with {@link Clone} are cloned. Thus, any
     * other fields may or may not be null.
     * <p/>
     * Also note that in the case of a clone for GWT, either the original object
     * is deemed GWT serializable and returned as is, or a deep copy is made.
     * Partial copies that weave GWT serializable parts of the original object
     * graph with copies of the non GWT serializable parts of the original graph
     * are not used. Although this may be done as a future optimization.
     * <p/>
     * Note that some Immutable objects (as defined above) are reused as is
     * without a copy being created, since they can't be modified.
     * 
     * @param <T>
     * @param source
     * @param projection
     *            The projection to apply. See {@link RecursiveReflexiveCloner}
     * @return The clone
     * @throws CloningError
     *             The object is not clonable for some reason
     */
    public <T> T copyForGwtRpcIfNeeded (T source,
            Class<? extends Projection> projection)
    {
        if (null == source)
        {
            return null;
        }

        Class<? extends Object> type = source.getClass();

        Cloner cloner = getClonerForType(type);

        if (!cloner.cloneNeededForGwt(source,
                projection,
                Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>())))
        {
            return source;
        }

        @SuppressWarnings("unchecked")
        T clone = (T)cloner.deepClone(source,
                projection,
                new IdentityHashMap<Object, Object>());

        return clone;
    }

    /**
     * Like {@link #deepClone(Object, Class)}, with a null projection.
     */
    public <T> T deepClone (T source)
    {
        return deepClone(source, null);
    }

    /**
     * Given an object, makes a clone of it. It uses reflection to walk the
     * object's members and clone them recursively if needed. The original
     * object graph is never modified.
     * <p/>
     * For a deep clone:
     * 
     * <pre>
     * Object orig;
     * Object clone = deepClone(orig);
     * clone==deepClone(orig)
     * </pre>
     * 
     * is always false.
     * <p/>
     * Note that only fields marked with {@link Clone} are cloned. Any other
     * fields in the clone will have their default value.
     * 
     * @param <T>
     * @param source
     * @param projection
     *            The projection to apply. See {@link RecursiveReflexiveCloner}
     * @return The clone
     * @throws CloningError
     *             The object is not clonable for some reason
     */
    public <T> T deepClone (T source, Class<? extends Projection> projection)
    {
        if (null == source)
        {
            return null;
        }

        Class<? extends Object> type = source.getClass();

        Cloner cloner = getClonerForType(type);

        @SuppressWarnings("unchecked")
        T clone = (T)cloner.deepClone(source,
                projection,
                new IdentityHashMap<Object, Object>());

        return clone;
    }

    /**
     * Like {@link #shallowCopyFieldsFromClient(Object, Object, Class)}, with a
     * null projection.
     */
    public <T> void shallowCopyFieldsFromClient (T destination, T source)
    {
        shallowCopyFieldsFromClient(destination, source, null);
    }

    /**
     * Given a destination object on the server and another source object of the
     * same type that came from the client, copies over all
     * {@link CopyFromClient} annotated fields from the source object to the
     * destination object.
     * <p/>
     * Note: This is not done recursively. I.e., this is a shallow copy, not a
     * deep copy.
     * 
     * @param <T>
     *            The type of the objects in question. This must be a class that
     *            is annotated {@link ReflexivelyClonable}.
     * @param destination
     *            The object of the server to overwrite. Must not be null.
     * @param source
     *            The object that came from the client. Will not be modified.
     *            Must not be null.
     * @param projection
     *            The projection to apply. See {@link RecursiveReflexiveCloner}
     * @throws CloningError
     *             The object is not clonable for some reason
     */
    public <T> void shallowCopyFieldsFromClient (T destination, T source,
            Class<? extends Projection> projection)
    {
        if (null == source)
        {
            throw new NullPointerException("source object cannot be null");
        }
        if (null == destination)
        {
            throw new NullPointerException("destination object cannot be null");
        }

        Class<? extends Object> type = source.getClass();

        Cloner cloner = getClonerForType(type);

        cloner.copyFromClient(destination, source, projection);
    }

    private Cloner getClonerForType (Class<?> type)
    {
        if (typeIsImmutable(type))
        {
            return immutableTypeCloner;
        }
        else if (type.equals(Date.class))
        {
            return dateCloner;
        }
        else if (type.isArray() && typeIsImmutable(type.getComponentType()))
        {
            return new SimpleArrayCloner(type.getComponentType(),
                    getClonerForType(type.getComponentType()));
        }
        else if (type.isArray())
        {
            return new ArrayCloner(type.getComponentType());
        }
        else if (typeIsRecursivelyCloned(type))
        {
            Cloner cloner;
            synchronized (cloningActions)
            {
                cloner = cloningActions.get(type);
                if (null == cloner)
                {
                    /*
                     * This is done in 2 phases to handle circular references.
                     * First, we construct a shell cloner and add it to the
                     * global hashmap. Then we initialize it, which will walk
                     * its fields reflexively and may call us again recursively.
                     * We do all of this with the lock held so no one tries to
                     * access a cloner while its not yet initialized.
                     */
                    cloner = new ReflexiveCloner(type);
                    cloningActions.put(type, cloner);
                    boolean done = false;
                    try
                    {
                        ((ReflexiveCloner)cloner).initialize();
                        done = true;
                    }
                    finally
                    {
                        if (!done)
                        {
                            /*
                             * If we had an initialization error, remove the
                             * shell cloner, so no one runs into it.
                             */
                            cloningActions.remove(type);
                        }
                    }
                }
            }
            return cloner;
        }
        else if (Collection.class.isAssignableFrom(type))
        {
            return javaUtilCollectionCloner;
        }
        else
        {
            throw new CloningError("Type " + type.getName()
                    + " not clonable. Did you forget to implement "
                    + ReflexivelyClonable.class.getName());
        }
    }

    private boolean typeIsRecursivelyCloned (Class<?> type)
    {
        return null != type.getAnnotation(ReflexivelyClonable.class);
    }

    private boolean typeIsImmutable (Class<?> type)
    {
        return (type.isPrimitive() || type.isEnum() || immutableTypes.contains(type));
    }
}
