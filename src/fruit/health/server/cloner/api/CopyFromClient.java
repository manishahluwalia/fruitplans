
package fruit.health.server.cloner.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import fruit.health.server.cloner.impl.RecursiveReflexiveCloner;

/**
 * Indicates that this field should be copied over from the client to the server
 * when using {@link RecursiveReflexiveCloner}. Not having this annotation on a
 * field means that the field in the server's object is left untouched. Having
 * this annotation means that the field in the server's object is overwritten
 * with the field on the client's object.
 * <p/>
 * Additionally, you can specify that the annotation only applies to certain
 * {@link Projection}s. The annotation then does not apply for any other
 * projections (including the default, unspecified projection). Not specifying
 * any projections in the annotation means it applies to all projections.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD })
public @interface CopyFromClient
{
    public Class<? extends Projection>[] value() default {};
}
