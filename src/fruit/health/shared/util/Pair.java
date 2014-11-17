
package fruit.health.shared.util;

import java.io.Serializable;

import fruit.health.server.cloner.api.Clone;
import fruit.health.server.cloner.api.ReflexivelyClonable;

@SuppressWarnings("serial")
@ReflexivelyClonable
public class Pair<A, B> implements Serializable
{
    @Clone private A a;
    @Clone private B b;

    public Pair ()
    {
        a = null;
        b = null;
    }

    public Pair (A a, B b)
    {
        this.a = a;
        this.b = b;
    }

    public A getA ()
    {
        return a;
    }

    public B getB ()
    {
        return b;
    }

    public void setA (A a)
    {
        this.a = a;
    }

    public void setB (B b)
    {
        this.b = b;
    }

    @Override
    public boolean equals (Object _that)
    {
        if (null == _that || !(_that instanceof Pair))
        {
            return false;
        }

        Pair<?, ?> that = (Pair<?, ?>)_that;

        if (null == a && null != that.a || !a.equals(that.a))
        {
            return false;
        }

        if (null == b && null != that.b || !b.equals(that.b))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode ()
    {
        int h = 0;
        if (null != a)
        {
            h += 2 * a.hashCode();
        }
        if (null != b)
        {
            h += b.hashCode();
        }
        return h;
    }
}
