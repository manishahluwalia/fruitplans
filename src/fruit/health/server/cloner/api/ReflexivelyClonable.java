
package fruit.health.server.cloner.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import fruit.health.server.cloner.impl.RecursiveReflexiveCloner;

/**
 * Annotation to indicate that this class can be cloned via
 * {@link RecursiveReflexiveCloner}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ReflexivelyClonable
{
}
