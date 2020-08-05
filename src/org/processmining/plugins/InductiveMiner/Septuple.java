package org.processmining.plugins.InductiveMiner;

public class Septuple<A,B,C,D,E,F,G> {
	private final A a;
	private final B b;
	private final C c;
	private final D d;
	private final E e;
	private final F f;
	private final G g;
	
	public Septuple(A a, B b, C c, D d, E e, F f, G g) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
		this.f = f;
		this.g = g;
	}
	
	public static <A,B,C,D,E,F,G> Septuple<A,B,C,D,E,F,G> of(A a, B b, C c, D d, E e, F f, G g){
        return new Septuple<A,B,C,D,E,F,G>(a,b,c,d,e,f,g);
    }
	
	public A getA() { return a; }
	public B getB() { return b; }
	public C getC() { return c; }
	public D getD() { return d; }
	public E getE() { return e; }
	public F getF() { return f; }
	public G getG() { return g; }

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		result = prime * result + ((c == null) ? 0 : c.hashCode());
		result = prime * result + ((d == null) ? 0 : d.hashCode());
		result = prime * result + ((e == null) ? 0 : e.hashCode());
		result = prime * result + ((f == null) ? 0 : f.hashCode());
		result = prime * result + ((g == null) ? 0 : g.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Septuple<?, ?, ?, ?, ?, ?, ?> other = (Septuple<?, ?, ?, ?, ?, ?, ?>) obj;
		if (a == null) {
			if (other.a != null)
				return false;
		} else if (!a.equals(other.a))
			return false;
		if (b == null) {
			if (other.b != null)
				return false;
		} else if (!b.equals(other.b))
			return false;
		if (c == null) {
			if (other.c != null)
				return false;
		} else if (!c.equals(other.c))
			return false;
		if (d == null) {
			if (other.d != null)
				return false;
		} else if (!d.equals(other.d))
			return false;
		if (e == null) {
			if (other.e != null)
				return false;
		} else if (!e.equals(other.e))
			return false;
		if (f == null) {
			if (other.f != null)
				return false;
		} else if (!f.equals(other.f))
			return false;
		if (g == null) {
			if (other.g != null)
				return false;
		} else if (!g.equals(other.g))
			return false;
		return true;
	}
	
	
}
