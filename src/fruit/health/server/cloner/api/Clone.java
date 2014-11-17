package fruit.health.server.cloner.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import fruit.health.server.cloner.impl.RecursiveReflexiveCloner;

/**
 * Indicates that this field should be cloned when using
 * {@link RecursiveReflexiveCloner}. Not having this annotation on a field means
 * that it may or may not be copied over. If you DO NOT want a field copied
 * over, use {@link DoNotClone}.
 * <p/>
 * The following section applies to both {@link Clone} and {@link DoNotClone}:
 * <p/>
 * Additionally, you can specify that the annotation only applies to certain
 * {@link Projection}s. The annotation then does not apply for any other
 * projections. Not specifying any projections in the annotation means it
 * applies to all projections not explicitly specified in the dual annotation.
 * The same projection cannot be specified in both {@link Clone} and
 * {@link DoNotClone}.
 * <p/>
 * As a corollary to the above, it is illegal to annotate a field both
 * {@link Clone} and {@link DoNotClone} without any projections.
 * <p/>
 * The following examples may make this clearer:
 * <p/>
 * 
 * <pre>
 * public interface ProjectionA extends Projection
 * {
 * }
 * 
 * public interface ProjectionB extends Projection
 * {
 * }
 * 
 * public interface ProjectionC extends Projection
 * {
 * }
 * 
 * &#064;ReflexivelyClonable
 * public class Model
 * {
 *     &#064;Clone({ ProjectionA.class, ProjectionB.class }) int x;
 * 
 *     &#064;Clone(ProjectionA.class) @DoNotClone(ProjectionB.class) int y;
 * }
 * </pre>
 * 
 * <table border="1">
 * <tr>
 * <th>Field</th>
 * <th>ProjectionA</th>
 * <th>ProjectionB</th>
 * <th>ProjectionC</th>
 * </tr>
 * <tr>
 * <td>x</td>
 * <td>cloned</td>
 * <td>cloned</td>
 * <td>don't care</td>
 * </tr>
 * <td>x</td>
 * <td>cloned</td>
 * <td>not cloned (is 0 in client copy)</td>
 * <td>don't care</td>
 * </tr>
 * </table>
 * <p/>
 * Note: that projection checking is done by using "==", so the exact projection
 * class must be used, not a derived class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD })
public @interface Clone
{
    public Class<? extends Projection>[] value() default {};
}
