package fruit.health.client.mvp;

/**
 * This is a marker interface. If Place wants the property that going to it when
 * you are already at the place causes the activity / view to be reloaded, then
 * it should extend this interface.
 * <p/>
 * This works by mucking with the {@link BasePlace#equals(Object)} routine:
 * Different instances of {@link ReloadingPlace} implementors are NEVER equal to
 * each other (same instances have to be equal to each other by Java rules: If
 * x==y, then x.equals(y) and y.equals(x)). Thereby causing GWT to not think
 * that the desired target place is not the same as the current place (even if
 * they are the same Place type and have {@link Object#equals(Object)} tokens).
 * Yes, this is a hack.
 * <p/>
 * As an example, in Gmail, if you hit "Compose", start typing in the email and
 * body hit "Compose" again, a whole new draft email is now created. "Compose"
 * is thus a {@link ReloadingPlace} in our terminology.
 * <p/>
 * Be very careful when you reuse the same instance of a {@link ReloadingPlace},
 * same instances are always equal to each other. You may therefore not get the
 * reloading you are looking for.
 */
public interface ReloadingPlace
{
}
