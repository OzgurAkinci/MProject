class Program {
  Function def;
  Program prog;
  public Program(Function x, Program y) {
    def = x;
    prog = y;
  }
  public Program copy() {
  	return new Program(def, prog);
  }
  public String toString() {
  	String p = "";
  	if (prog != null)
  	  p = "" + prog;

    return "new Program(" + def + "," + p + ")";
  }
}

class Function {
  Header f;
  Stm s;
  QList eq;
  public Function(Header x, Stm y, QList z) {
    f = x;
    s = y;
    eq = z;
  }
  public Function copy() {
  	return new Function(f, s, eq);
  }
  //public void accept(Visitor v) {
  //  v.visit(this);
  //}
  public String toString() {
  	String q = "[],[]";
  	if (eq != null)
  	  q = "" + eq;

    return "new Function(" + f + "," + s + "," + q + ")";
  }
}

class EList {
  Exp e;
  EList eq;
  public EList(Exp x, EList y) {
    e = x;
    eq = y;
  }
  public String toString() {
  	String s = "";
    EList eq2 = eq;
  	while (eq2 != null) {
        s += "," + eq2.e;
        eq2 = eq2.eq;  	  
  	}
  	if (s.length() > 0)
  	  s = s.substring(1);
  	
  	return "[" + s + "]";
  }
}

class QList {
  BExp b;
  Exp e;
  QList eq;
  public QList(BExp x, Exp y, QList z) {
    b = x;
    e = y;
    eq = z;
  }
  public String toString() {
  	String s1 = "", s2 = "";
    QList eq2 = eq;
  	while (eq2 != null) {
        s1 += "," + eq2.b;
        s2 += "," + eq2.e;
        eq2 = eq2.eq;  	  
  	}
  	if (s1.length() > 0)
  	  s1 = s1.substring(1);
  	if (s2.length() > 0)
  	  s2 = s2.substring(1);
  	
  	return "[" + s1 + "],[" + s2 + "]";
  }
}

abstract class Stm {
  public abstract void accept(Visitor v);
  public String toString() { return ""; }
}

class LStm extends Stm {
  Stm a, b;
  public LStm(Stm x, Stm y) {
    a = x;
    b = y;
  }
  public LStm copy() {
  	return new LStm(a, b);
  }
  public void accept(Visitor v) {
    v.visit(this);
  }
  public String toString() {
    return "new LStm(" + a + "," + b + ")";
  }
}

class AStm extends Stm {
  String id;
  Exp e;
  public AStm(String x, Exp y) {
    id = x;
    e = y;
  }
  public AStm copy() {
  	return new AStm(id, e);
  }
  public void accept(Visitor v) {
    v.visit(this);
  }
  public String toString() {
    return "new AStm(" + id + "," + e + ")";
  }
}

class PStm extends Stm {
  EList eq;
  public PStm(EList x) {
    eq = x;
  }
  public PStm copy() {
  	return new PStm(eq);
  }
  public void accept(Visitor v) {
    v.visit(this);
  }
  public String toString() {
    String q = "[],[]";
  	if (eq != null)
  	  q = "" + eq;

    return "new PStm(" + q + ")";
  }
}

class Exp {
  public Object accept(Visitor v) { return null; }
  public Exp copy() {
  	return new Exp();
  }
  public String toString() { return ""; }
}

class Header extends Exp {
  String id;
  EList eq;
  public Header(String x, EList y) {
    id = x;
    eq = y;
  }
  public Header copy() {
  	return new Header(id, eq);
  }
  public Object accept(Visitor v) {
    return v.visit(this);
  }
  public String toString() {
  	String q = "[],[]";
  	if (eq != null)
  	  q = "" + eq;

    return "new Header(" + id + "," + q + ")";
  }
}

class Plus extends Exp {
  Exp a, b;
  public Plus(Exp x, Exp y) {
    a = x;
    b = y;
  }
  public Plus copy() {
  	return new Plus(a, b);
  }
  public Object accept(Visitor v) {
    return v.visit(this);
  }
  public String toString() {
    return "new Plus(" + a + "," + b + ")";
  }
}

class Minus extends Exp {
  Exp a, b;
  public Minus(Exp x, Exp y) {
    a = x;
    b = y;
  }
  public Minus copy() {
  	return new Minus(a, b);
  }
  public Object accept(Visitor v) {
    return v.visit(this);
  }
  public String toString() {
    return "new Minus(" + a + "," + b + ")";
  }
}

class Times extends Exp {
  Exp a, b;
  public Times(Exp x, Exp y) {
    a = x;
    b = y;
  }
  public Times copy() {
  	return new Times(a, b);
  }
  public Object accept(Visitor v) {
    return v.visit(this);
  }
  public String toString() {
    return "new Times(" + a + "," + b + ")";
  }
}

class Divide extends Exp {
  Exp a, b;
  public Divide(Exp x, Exp y) {
    a = x;
    b = y;
  }
  public Divide copy() {
  	return new Divide(a, b);
  }
  public Object accept(Visitor v) {
    return v.visit(this);
  }
  public String toString() {
    return "new Divide(" + a + "," + b + ")";
  }
}

class Mod extends Exp {
  Exp a, b;
  public Mod(Exp x, Exp y) {
    a = x;
    b = y;
  }
  public Mod copy() {
  	return new Mod(a, b);
  }
  public Object accept(Visitor v) {
    return v.visit(this);
  }
  public String toString() {
    return "new Mod(" + a + "," + b + ")";
  }
}

class Power extends Exp {
  Exp a, b;
  boolean[] visited = null;
  public Power(Exp x, Exp y) {
    a = x;
    b = y;
    if (y instanceof Num)
      visited = new boolean[((Num)y).n];
  }
  public Power copy() {
  	return new Power(a, b);
  }
  public Object accept(Visitor v) {
    return v.visit(this);
  }
  public String toString() {
    return "new Power(" + a + "," + b + ")";
  }
}

class Var extends Exp {
  String id;
  public Var(String x) {
    id = x;
  }
  public Var copy() {
  	return new Var(id);
  }
  public Object accept(Visitor v) {
    return v.visit(this);
  }
  public String toString() {
    return "new Var(" + id + ")";
  }
}

class Num extends Exp {
  int n;
  public Num(int x) {
    n = x;
  }
  public Num copy() {
  	return new Num(n);
  }
  public Object accept(Visitor v) {
    return v.visit(this);
    //return new Integer(n);
  }
  public String toString() {
    return "new Num(" + n + ")";
  }
}

class DNum extends Exp {
  double n;
  public DNum(double x) {
    n = x;
  }
  public DNum copy() {
  	return new DNum(n);
  }
  public Object accept(Visitor v) {
    return v.visit(this);
    //return new Double(n);
  }
  public String toString() {
    return "new DNum(" + n + ")";
  }
}

class Str extends Exp {
  String s;
  public Str(String x) {
    s = x;
  }
  public Str copy() {
  	return new Str(s);
  }
  public Object accept(Visitor v) {
    return v.visit(this);
    //return s;
  }
  public String toString() {
    return "new Str(" + s + ")";
  }
}

class Ln extends Exp {
  Exp a;
  public Ln(Exp x) {
	a = x;
  }
  public Ln copy() {
  	return new Ln(a);
  }
  public Object accept(Visitor v) {
	return v.visit(this);
  }
  public String toString() {
    return "new Ln(" + a + ")";
  }
}

class Log extends Exp {
  Exp a;
  public Log(Exp x) {
	a = x;
  }
  public Log copy() {
  	return new Log(a);
  }
  public Object accept(Visitor v) {
	return v.visit(this);
  }
  public String toString() {
    return "new Log(" + a + ")";
  }
}

class Ep extends Exp {
  Exp a;
  public Ep(Exp x) {
	a = x;
  }
  public Ep copy() {
  	return new Ep(a);
  }
  public Object accept(Visitor v) {
	return v.visit(this);
  }
  public String toString() {
    return "new Ep(" + a + ")";
  }
}

class Sin extends Exp {
  Exp a;
  public Sin(Exp x) {
	a = x;
  }
  public Sin copy() {
  	return new Sin(a);
  }
  public Object accept(Visitor v) {
	return v.visit(this);
  }
  public String toString() {
    return "new Sin(" + a + ")";
  }
}

class Cos extends Exp {
  Exp a;
  public Cos(Exp x) {
	a = x;
  }
  public Cos copy() {
  	return new Cos(a);
  }
  public Object accept(Visitor v) {
	return v.visit(this);
  }
  public String toString() {
    return "new Cos(" + a + ")";
  }
}

class Tan extends Exp {
  Exp a;
  public Tan(Exp x) {
	a = x;
  }
  public Tan copy() {
  	return new Tan(a);
  }
  public Object accept(Visitor v) {
	return v.visit(this);
  }
  public String toString() {
    return "new Tan(" + a + ")";
  }
}

class Abs extends Exp {
  Exp a;
  public Abs(Exp x) {
    a = x;
  }
  public Abs copy() {
  	return new Abs(a);
  }
  public Object accept(Visitor v) {
    return v.visit(this);
  }
  public String toString() {
    return "new Abs(" + a + ")";
  }
}

class Sqrt extends Exp {
  Exp a;
  public Sqrt(Exp x) {
	a = x;
  }
  public Sqrt copy() {
  	return new Sqrt(a);
  }
  public Object accept(Visitor v) {
	return v.visit(this);
  }
  public String toString() {
    return "new Sqrt(" + a + ")";
  }
}

class Round extends Exp {
  Exp a;
  int n;
  public Round(Exp x, int y) {
	a = x;
	n = y;
  }
  public Round copy() {
  	return new Round(a, n);
  }
  public Object accept(Visitor v) {
	return v.visit(this);
  }
  public String toString() {
    return "new Round(" + a + "," + n + ")";
  }
}

class DrvExp extends Exp {
  Exp e;
  int n;
  String id;
  public DrvExp(Exp x, int y, String z) {
    e = x;
    n = y;
    id = z;
  }
  public DrvExp copy() {
  	return new DrvExp(e, n, id);
  }
  public Object accept(Visitor v) {
    return v.visit(this);
  }
  public String toString() {
    return "new DrvExp(" + e + "," + n + "," + id + ")";
  }
}

abstract class BExp {
  public abstract boolean accept(Visitor v);
  public String toString() { return ""; }
}

class EQExp extends BExp {
  Exp a, b;
  public EQExp(Exp x, Exp y) {
    a = x;
    b = y;
  }
  public EQExp copy() {
  	return new EQExp(a, b);
  }
  public boolean accept(Visitor v) {
    return v.visit(this);
  }
  public String toString() {
    return "new EQExp(" + a + "," + b + ")";
  }
}

class NEExp extends BExp {
  Exp a, b;
  public NEExp(Exp x, Exp y) {
    a = x;
    b = y;
  }
  public NEExp copy() {
  	return new NEExp(a, b);
  }
  public boolean accept(Visitor v) {
    return v.visit(this);
  }
  public String toString() {
    return "new NEExp(" + a + "," + b + ")";
  }
}

class LEExp extends BExp {
  Exp a, b;
  public LEExp(Exp x, Exp y) {
    a = x;
    b = y;
  }
  public LEExp copy() {
  	return new LEExp(a, b);
  }
  public boolean accept(Visitor v) {
    return v.visit(this);
  }
  public String toString() {
    return "new LEExp(" + a + "," + b + ")";
  }
}

class LTExp extends BExp {
  Exp a, b;
  public LTExp(Exp x, Exp y) {
    a = x;
    b = y;
  }
  public LTExp copy() {
  	return new LTExp(a, b);
  }
  public boolean accept(Visitor v) {
    return v.visit(this);
  }
  public String toString() {
    return "new LTExp(" + a + "," + b + ")";
  }
}

class GTExp extends BExp {
  Exp a, b;
  public GTExp(Exp x, Exp y) {
    a = x;
    b = y;
  }
  public GTExp copy() {
  	return new GTExp(a, b);
  }
  public boolean accept(Visitor v) {
    return v.visit(this);
  }
  public String toString() {
    return "new GTExp(" + a + "," + b + ")";
  }
}

class GEExp extends BExp {
  Exp a, b;
  public GEExp(Exp x, Exp y) {
    a = x;
    b = y;
  }
  public GEExp copy() {
  	return new GEExp(a, b);
  }
  public boolean accept(Visitor v) {
    return v.visit(this);
  }
  public String toString() {
    return "new GEExp(" + a + "," + b + ")";
  }
}

class AndExp extends BExp {
  BExp a, b;
  public AndExp(BExp x, BExp y) {
    a = x;
    b = y;
  }
  public AndExp copy() {
  	return new AndExp(a, b);
  }
  public boolean accept(Visitor v) {
    return v.visit(this);
  }
  public String toString() {
    return "new AndExp(" + a + "," + b + ")";
  }
}

class OrExp extends BExp {
  BExp a, b;
  public OrExp(BExp x, BExp y) {
    a = x;
    b = y;
  }
  public OrExp copy() {
  	return new OrExp(a, b);
  }
  public boolean accept(Visitor v) {
    return v.visit(this);
  }
  public String toString() {
    return "new OrExp(" + a + "," + b + ")";
  }
}

class NotExp extends BExp {
  BExp a;
  public NotExp(BExp x) {
    a = x;
  }
  public NotExp copy() {
  	return new NotExp(a);
  }
  public boolean accept(Visitor v) {
    return v.visit(this);
  }
  public String toString() {
    return "new NotExp(" + a + ")";
  }
}

class BNum extends BExp {
  boolean b;
  public BNum(boolean x) {
    b = x;
  }
  public BNum copy() {
  	return new BNum(b);
  }
  public boolean accept(Visitor v) {
    //return v.visit(this);
    return b;
  }
  public String toString() {
    return "new BNum(" + (b ? "true" : "false") + ")";
  }
}
