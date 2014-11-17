
package fruit.health.server.cloner.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import fruit.health.server.cloner.impl.RecursiveReflexiveCloner;

/**
 * Indicates that this field should NOT be cloned when using
 * {@link RecursiveReflexiveCloner}. Not having this annotation on a field means
 * that it may or may not be copied over. If you want a field copied over, use
 * {@link Clone}.
 * <p/>
 * See {@link Clone} for full rules.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD })
public @interface DoNotClone
{
    public Class<? extends Projection>[] value() default {};
}
