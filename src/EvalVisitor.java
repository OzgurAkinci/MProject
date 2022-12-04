import java.util.Hashtable;

public class EvalVisitor implements Visitor  {
  static boolean DEBUG = false;
  boolean error = false;
  Program prog;
  SymTable t;

  public EvalVisitor(Program p, SymTable tb) {
  	prog = p;
  	t = tb;
  }

  public void visit(Stm s) {
  	if (error)
  		return;
    s.accept(this);
  }

  public void visit(LStm s) {
  	if (error)
  		return;
    s.a.accept(this);
    s.b.accept(this);
  }

  public void visit(AStm s) {
  	if (error)
  		return;
    t.put(s.id, s.e.accept(this));
  }

  public void visit(PStm s) {
  	if (error)
  		return;
  	EList eq = s.eq;
  	if (eq != null) {
  	  while (eq != null) {
        Object a = eq.e.accept(this);
        if (a instanceof Integer)
  	      System.out.print(((Integer)a).intValue() + " ");
  	    else if (a instanceof Double)
  	    {
  	      java.text.DecimalFormat df = new java.text.DecimalFormat("#.###");
  	      System.out.print(df.format(((Double)a).doubleValue()) + " ");
  	    }
  	    else
  	      System.out.print(((String)a) + " ");
  	    eq = eq.eq;
      }
  	}
  	else
      System.out.println();
  }

  public Object visit(Exp e) {
  	if (error)
  		return null;
    return e.accept(this);
  }
  
  public Object visit(Header fr) {
  	if (error || prog == null)
  		return null;
  		
  	int argi = 0;
  	Object[] argn = new Object[10];
  	String[] args = new String[10];

  	error = false;
  	Object res = null;
  	Program p = prog;
  	while (p != null) {
      if (p == null) {
      	if (res == null)
      	  System.out.println(fr.id +": undefined or missing parameters!");
      	error = true;
        break;
      }
      Header fl = p.def.f;
      if (fl.id.equals(fr.id)) {
      	argi = 0;
      	res = null;
      	boolean OK = true;
      	if (fl.eq != null && fr.eq != null) {
      	  EList eql = fl.eq;
      	  EList eqr = fr.eq;
      	  while (eql != null) {
      	    if (eql.e == null && eqr.e == null)
      	      break;

            if (eql.e != null && eqr.e != null) {
      	  	  //Object a = this.visit(eqr.e);
      	  	  Object a = eqr.e.accept(this);
      	  	  if (a instanceof Double && (((Double)a).isInfinite() || ((Double)a).isNaN()))
      	  	  	return null;
      	  	  if (eql.e instanceof Plus) {
      	  	  	args[argi] = ((Var)((Plus)(eql.e)).a).id;
      	  	  	if (a instanceof Integer)
      	  	  	  argn[argi++] = new Integer(((Integer)a).intValue() - ((Num)((Plus)(eql.e)).b).n);
      	  	  	else if (a instanceof Double)
      	  	  	  argn[argi++] = new Double(((Double)a).doubleValue() - ((DNum)((Plus)(eql.e)).b).n);
      	  	  	else
      	  	  	  argn[argi++] = (String)a;
      	  	  }
      	  	  else if (eql.e instanceof Var) {
      	  	  	args[argi] = ((Var)(eql.e)).id;
      	  	  	argn[argi++] = a;
      	  	  }
      	  	  else {//if (eql.e instanceof Num)
      	  	    args[argi] = "";  //parameter bir deger olarak girilmisse
      	  	  	argn[argi++] = a;
      	  	  	if (a instanceof Integer) {
      	  	  	  if (((Integer)a).intValue() != ((Num)(eql.e)).n) {
      	  	  	  	OK = false;
      	  	        break;
      	  	  	  }
      	  	  	}
      	  	  	if (a instanceof Double) {
      	  	  	  if (((Double)a).doubleValue() != ((DNum)(eql.e)).n) {
      	  	  	    OK = false;
      	  	        break;
      	  	  	  }
      	  	  	}
      	  	  	if (a instanceof String) {
      	  	  	  if (!((String)a).equals(((Str)(eql.e)).s)) {
      	  	  	    OK = false;
      	  	        break;
      	  	  	  }
      	  	  	}
      	  	  }
      	    }
      	    else {
      	      OK = false;
      	      break;
      	    }
      	    eql = eql.eq;
      	    eqr = eqr.eq;
      	  }
      	}
      	if (OK) {
      	  t.beginScope();
      	  String arg = "";
      	  for (int j = 0; j < argi; j++) {
      	  	if (args[j].equals(""))
      	  		continue;
      	  	if (argn[j] instanceof Integer) {
      	  	  arg += "," + ((Integer)argn[j]).intValue();
      	  	  t.put(args[j], argn[j]);
      	  	}
      	  	else if (argn[j] instanceof Double) {
      	  	  arg += "," + ((Double)argn[j]).doubleValue();
      	  	  t.put(args[j], argn[j]);
      	    }
      	    else {
      	      arg += "," + (String)argn[j];
      	  	  t.put(args[j], argn[j]);
      	    }
      	  }
      	  if (DEBUG) {
      	    System.out.println("....Calling " + fl.id + "(" + (argi>0?arg.substring(1):"") + ") Scope: " + t.n);
      	    System.out.println("....Object: " + p.def);
      	  }
      	  if (p.def.s != null)
      	    //this.visit(p.def.s);
      	    p.def.s.accept(this);
      	  
      	  QList eq = p.def.eq;
      	  while (eq != null) {
      	    if (eq.b.accept(this)) {
      	      //res = this.visit(p.def.eq);
      	      res = eq.e.accept(this);
      	      break;
      	    }
      	    eq = eq.eq;
      	  }
      	  
      	  if (DEBUG)
      	    System.out.println("Ending " + fl.id + "(" + (argi>0?arg.substring(1):"") + ") Scope: " + t.n);
      	  t.endScope();
      	  break;
      	}
      }
      p = p.prog;
  	}
  	
  	return res;
  }

  public Object visit(Plus e) {
  	if (error)
  		return null;
  	Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a instanceof Integer && b instanceof Integer) {
  	  int x = ((Integer)a).intValue();
  	  int y = ((Integer)b).intValue();
  	  return new Integer(x+y);
  	}
  	else if (a instanceof String || b instanceof String) {
  	  String x, y;
  	  if (a instanceof Integer)
  	    x = ((Integer)a).intValue() + "";
  	  else if (a instanceof Double)
  	    x = ((Double)a).doubleValue() + "";
  	  else
  	  	x = (String)a;
  	  if (b instanceof Integer)
  	    y = ((Integer)b).intValue() + "";
  	  else if (b instanceof Double)
  	    y = ((Double)b).doubleValue() + "";
  	  else
  	  	y = (String)b;
  	  return x + y;
  	}
  	else {
  	  double x, y;
  	  if (a instanceof Integer)
  	    x = ((Integer)a).intValue();
  	  else
  	    x = ((Double)a).doubleValue();
  	  if (b instanceof Integer)
  	  	y = ((Integer)b).intValue();
  	  else
  	    y = ((Double)b).doubleValue();
  	  return new Double(x+y);
  	}
  }

  public Object visit(Minus e) {
  	if (error)
  		return null;
  	Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a instanceof Integer && b instanceof Integer) {
  	  int x = ((Integer)a).intValue();
  	  int y = ((Integer)b).intValue();
  	  return new Integer(x-y);
  	}
  	else if (a instanceof String || b instanceof String) {
  	  return "";
  	}
  	else {
  	  double x, y;
  	  if (a instanceof Integer)
  	    x = ((Integer)a).intValue();
  	  else
  	    x = ((Double)a).doubleValue();
  	  if (b instanceof Integer)
  	  	y = ((Integer)b).intValue();
  	  else
  	    y = ((Double)b).doubleValue();
  	  return new Double(x-y);
  	}
  }

  public Object visit(Times e) {
  	if (error)
  		return null;
  	Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a instanceof Integer && b instanceof Integer) {
  	  int x = ((Integer)a).intValue();
  	  int y = ((Integer)b).intValue();
  	  return new Integer(x*y);
  	}
  	else if (a instanceof String || b instanceof String) {
  	  return "";
  	}
  	else {
  	  double x, y;
  	  if (a instanceof Integer)
  	    x = ((Integer)a).intValue();
  	  else
  	    x = ((Double)a).doubleValue();
  	  if (b instanceof Integer)
  	  	y = ((Integer)b).intValue();
  	  else
  	    y = ((Double)b).doubleValue();
  	  return new Double(x*y);
  	}
  }

  public Object visit(Divide e) {
  	if (error)
  		return null;
  	Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a instanceof Integer && b instanceof Integer) {
  	  int x = ((Integer)a).intValue();
  	  int y = ((Integer)b).intValue();
  	  //return new Integer(x/y);
  	  return new Double(x*1.0/y);
  	}
  	else if (a instanceof String || b instanceof String) {
  	  return "";
  	}
  	else {
  	  double x, y;
  	  if (a instanceof Integer)
  	    x = ((Integer)a).intValue();
  	  else
  	    x = ((Double)a).doubleValue();
  	  if (b instanceof Integer)
  	  	y = ((Integer)b).intValue();
  	  else
  	    y = ((Double)b).doubleValue();
  	  //return new Double(x/y);
  	  return new Double(x*1.0/y);
  	}
  }

  public Object visit(Mod e) {
  	if (error)
  		return null;
  	Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a instanceof Integer && b instanceof Integer) {
  	  int x = ((Integer)a).intValue();
  	  int y = ((Integer)b).intValue();
  	  return new Integer(x%y);
  	}
  	else if (a instanceof String || b instanceof String) {
  	  return "";
  	}
  	else {
  	  double x, y;
  	  if (a instanceof Integer)
  	    x = ((Integer)a).intValue();
  	  else
  	    x = ((Double)a).doubleValue();
  	  if (b instanceof Integer)
  	  	y = ((Integer)b).intValue();
  	  else
  	    y = ((Double)b).doubleValue();
  	  return new Double(((int)x)%((int)y));
  	}
  }

  public Object visit(Power e) {
  	if (error)
  		return null;
  	Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a instanceof Integer && b instanceof Integer) {
  	  int x = ((Integer)a).intValue();
  	  int y = ((Integer)b).intValue();
  	  return new Integer((int)(Math.pow(x, y)));
  	}
  	else if (a instanceof String || b instanceof String) {
  	  return "";
  	}
  	else {
  	  double x, y;
  	  if (a instanceof Integer)
  	    x = ((Integer)a).intValue();
  	  else
  	    x = ((Double)a).doubleValue();
  	  if (b instanceof Integer)
  	  	y = ((Integer)b).intValue();
  	  else
  	    y = ((Double)b).doubleValue();
  	  return new Double(Math.pow(x, y));
  	}
  }
  
  public Object visit(Var e) {
  	if (error)
  		return null;
    return t.get(e.id);
  }

  public Object visit(Num e) {
  	if (error)
  		return null;
    return new Integer(e.n);
  }
  
  public Object visit(DNum e) {
  	if (error)
  		return null;
    return new Double(e.n);
  }
  
  public Object visit(Str e) {
  	if (error)
  		return null;
    return e.s;
  }

  public Object visit(Ln e) {
  	if (error)
  		return null;
	Object a = e.a.accept(this);
	if (a instanceof Integer)
	  return new Double(Math.log(((Integer)a).intValue()));
	else if (a instanceof Double)
	  return new Double(Math.log(((Double)a).doubleValue()));
	else
	  return (String)a;			  
  }

  public Object visit(Log e) {
  	if (error)
  		return null;
	Object a = e.a.accept(this);
	if (a instanceof Integer)
	  return new Double(Math.log10(((Integer)a).intValue()));
	else if (a instanceof Double)
	  return new Double(Math.log10(((Double)a).doubleValue()));
	else
	  return (String)a;			  
  }

  public Object visit(Ep e) {
  	if (error)
  		return null;
	Object a = e.a.accept(this);
	if (a instanceof Integer)
	  return new Double(Math.pow(Math.E, ((Integer)a).intValue()));
	else if (a instanceof Double)
	  return new Double(Math.pow(Math.E, ((Double)a).doubleValue()));
	else
	  return (String)a;
  }

  public Object visit(Sin e) {
  	if (error)
  		return null;
	Object a = e.a.accept(this);
	if (a instanceof Integer)
	  return new Double(Math.sin(((Integer)a).intValue()));
	else if (a instanceof Double)
	  return new Double(Math.sin(((Double)a).doubleValue()));
	else
	  return (String)a;
  }

  public Object visit(Cos e) {
  	if (error)
  		return null;
	Object a = e.a.accept(this);
	if (a instanceof Integer)
	  return new Double(Math.cos(((Integer)a).intValue()));
	else if (a instanceof Double)
	  return new Double(Math.cos(((Double)a).doubleValue()));
	else
	  return (String)a;
  }

  public Object visit(Tan e) {
  	if (error)
  		return null;
	Object a = e.a.accept(this);
	if (a instanceof Integer)
	  return new Double(Math.tan(((Integer)a).intValue()));
	else if (a instanceof Double)
	  return new Double(Math.tan(((Double)a).doubleValue()));
	else
	  return (String)a;
  }

  public Object visit(Abs e) {
  	if (error)
  		return null;
  	Object a = e.a.accept(this);
  	if (a instanceof Integer)
  	  return new Integer(Math.abs(((Integer)(a)).intValue()));
  	else if (a instanceof Double)
  	  return new Double(Math.abs(((Double)(a)).doubleValue()));
  	else
  	  return (String)a;
  }

  public Object visit(Sqrt e) {
  	if (error)
  		return null;
	Object a = e.a.accept(this);
	if (a instanceof Integer)
	  return new Double(Math.sqrt(((Integer)a).intValue()));
	else if (a instanceof Double)
	  return new Double(Math.sqrt(((Double)a).doubleValue()));
	else
	  return (String)a;
  }

  public Object visit(Round e) {
  	if (error)
  		return null;
	Object a = e.a.accept(this);
	if (a instanceof Integer)
	  return new Double(Math.sqrt(((Integer)a).intValue()));
	else if (a instanceof Double) {
	  if (e.n <= 0)
		return new Double(Math.round(((Double)a).doubleValue()));
	  else if (e.n > 15)
	    return new Double(((Double)a).doubleValue());
	  else {
		String sym = "###############";
	    java.text.DecimalFormat df = new java.text.DecimalFormat("#." + sym.substring(0,e.n));
		df.setRoundingMode(java.math.RoundingMode.CEILING);
		return new Double(df.format(((Double)a).doubleValue()));
	  }
	}
	else
	  return (String)a;
  }
		  
  public Object visit(DrvExp e) {
  	if (error)
  		return null;
    DeriveVisitor dvisitor = new DeriveVisitor(prog, t, e.id);
    Exp exp = e.e;
    for (int i = 0; i < e.n; i++) {
   	  //Exp de = (Exp)(dvisitor.visit(exp));
   	  //SimplifyVisitor svisitor = new SimplifyVisitor();
   	  //Exp se = (Exp)(svisitor.visit(de));
   	  //PrintVisitor pvisitor = new PrintVisitor();
   	  //String pe = pvisitor.visit(se);
   	  //System.out.println(i + "-> " + pe);
   	  //exp = se;
	  exp = (Exp)(dvisitor.visit(exp));
    }
    
    return exp.accept(this);

//return null;
  }
  
  public boolean visit(BExp e) {
  	if (error)
  		return false;
    return e.accept(this);
  }
  
  public boolean visit(EQExp e) {
  	if (error)
  		return false;
  	Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a instanceof Integer && b instanceof Integer) {
  	  int x = ((Integer)a).intValue();
  	  int y = ((Integer)b).intValue();
  	  return x == y;
  	}
  	else if (a instanceof String || b instanceof String) {
  	  String x, y;
  	  if (a instanceof Integer)
  	    x = ((Integer)a).intValue() + "";
  	  else if (a instanceof Double)
  	    x = ((Double)a).doubleValue() + "";
  	  else
  	  	x = (String)a;
  	  if (b instanceof Integer)
  	    y = ((Integer)b).intValue() + "";
  	  else if (b instanceof Double)
  	    y = ((Double)b).doubleValue() + "";
  	  else
  	  	y = (String)b;
  	  return x.equals(y);
  	}
  	else {
  	  double x, y;
  	  if (a instanceof Integer)
  	    x = ((Integer)a).intValue();
  	  else
  	    x = ((Double)a).doubleValue();
  	  if (b instanceof Integer)
  	  	y = ((Integer)b).intValue();
  	  else
  	    y = ((Double)b).doubleValue();
  	  return x == y;
  	}
  }
  
  public boolean visit(NEExp e) {
  	if (error)
  		return false;
  	Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a instanceof Integer && b instanceof Integer) {
  	  int x = ((Integer)a).intValue();
  	  int y = ((Integer)b).intValue();
  	  return x != y;
  	}
  	else if (a instanceof String || b instanceof String) {
  	  String x, y;
  	  if (a instanceof Integer)
  	    x = ((Integer)a).intValue() + "";
  	  else if (a instanceof Double)
  	    x = ((Double)a).doubleValue() + "";
  	  else
  	  	x = (String)a;
  	  if (b instanceof Integer)
  	    y = ((Integer)b).intValue() + "";
  	  else if (b instanceof Double)
  	    y = ((Double)b).doubleValue() + "";
  	  else
  	  	y = (String)b;
  	  return !x.equals(y);
  	}
  	else {
  	  double x, y;
  	  if (a instanceof Integer)
  	    x = ((Integer)a).intValue();
  	  else
  	    x = ((Double)a).doubleValue();
  	  if (b instanceof Integer)
  	  	y = ((Integer)b).intValue();
  	  else
  	    y = ((Double)b).doubleValue();
  	  return x != y;
  	}
  }
  
  public boolean visit(LEExp e) {
  	if (error)
  		return false;
  	Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a instanceof Integer && b instanceof Integer) {
  	  int x = ((Integer)a).intValue();
  	  int y = ((Integer)b).intValue();
  	  return x < y;
  	}
  	else if (a instanceof String || b instanceof String) {
  	  String x, y;
  	  if (a instanceof Integer)
  	    x = ((Integer)a).intValue() + "";
  	  else if (a instanceof Double)
  	    x = ((Double)a).doubleValue() + "";
  	  else
  	  	x = (String)a;
  	  if (b instanceof Integer)
  	    y = ((Integer)b).intValue() + "";
  	  else if (b instanceof Double)
  	    y = ((Double)b).doubleValue() + "";
  	  else
  	  	y = (String)b;
  	  return x.compareTo(y) < 0;
  	}
  	else {
  	  double x, y;
  	  if (a instanceof Integer)
  	    x = ((Integer)a).intValue();
  	  else
  	    x = ((Double)a).doubleValue();
  	  if (b instanceof Integer)
  	  	y = ((Integer)b).intValue();
  	  else
  	    y = ((Double)b).doubleValue();
  	  return x < y;
  	}
  }
  
  public boolean visit(LTExp e) {
  	if (error)
  		return false;
  	Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a instanceof Integer && b instanceof Integer) {
  	  int x = ((Integer)a).intValue();
  	  int y = ((Integer)b).intValue();
  	  return x <= y;
  	}
  	else if (a instanceof String || b instanceof String) {
  	  String x, y;
  	  if (a instanceof Integer)
  	    x = ((Integer)a).intValue() + "";
  	  else if (a instanceof Double)
  	    x = ((Double)a).doubleValue() + "";
  	  else
  	  	x = (String)a;
  	  if (b instanceof Integer)
  	    y = ((Integer)b).intValue() + "";
  	  else if (b instanceof Double)
  	    y = ((Double)b).doubleValue() + "";
  	  else
  	  	y = (String)b;
  	  return x.compareTo(y) <= 0;
  	}
  	else {
  	  double x, y;
  	  if (a instanceof Integer)
  	    x = ((Integer)a).intValue();
  	  else
  	    x = ((Double)a).doubleValue();
  	  if (b instanceof Integer)
  	  	y = ((Integer)b).intValue();
  	  else
  	    y = ((Double)b).doubleValue();
  	  return x <= y;
  	}
  }

  public boolean visit(GTExp e) {
  	if (error)
  		return false;
    Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a instanceof Integer && b instanceof Integer) {
  	  int x = ((Integer)a).intValue();
  	  int y = ((Integer)b).intValue();
  	  return x >= y;
  	}
  	else if (a instanceof String || b instanceof String) {
  	  String x, y;
  	  if (a instanceof Integer)
  	    x = ((Integer)a).intValue() + "";
  	  else if (a instanceof Double)
  	    x = ((Double)a).doubleValue() + "";
  	  else
  	  	x = (String)a;
  	  if (b instanceof Integer)
  	    y = ((Integer)b).intValue() + "";
  	  else if (b instanceof Double)
  	    y = ((Double)b).doubleValue() + "";
  	  else
  	  	y = (String)b;
  	  return x.compareTo(y) >= 0;
  	}
  	else {
  	  double x, y;
  	  if (a instanceof Integer)
  	    x = ((Integer)a).intValue();
  	  else
  	    x = ((Double)a).doubleValue();
  	  if (b instanceof Integer)
  	  	y = ((Integer)b).intValue();
  	  else
  	    y = ((Double)b).doubleValue();
  	  return x >= y;
  	}
  }
  
  public boolean visit(GEExp e) {
  	if (error)
  		return false;
  	Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a instanceof Integer && b instanceof Integer) {
  	  int x = ((Integer)a).intValue();
  	  int y = ((Integer)b).intValue();
  	  return x > y;
  	}
  	else if (a instanceof String || b instanceof String) {
  	  String x, y;
  	  if (a instanceof Integer)
  	    x = ((Integer)a).intValue() + "";
  	  else if (a instanceof Double)
  	    x = ((Double)a).doubleValue() + "";
  	  else
  	  	x = (String)a;
  	  if (b instanceof Integer)
  	    y = ((Integer)b).intValue() + "";
  	  else if (b instanceof Double)
  	    y = ((Double)b).doubleValue() + "";
  	  else
  	  	y = (String)b;
  	  return x.compareTo(y) > 0;
  	}
  	else {
  	  double x, y;
  	  if (a instanceof Integer)
  	    x = ((Integer)a).intValue();
  	  else
  	    x = ((Double)a).doubleValue();
  	  if (b instanceof Integer)
  	  	y = ((Integer)b).intValue();
  	  else
  	    y = ((Double)b).doubleValue();
  	  return x > y;
  	}
  }

  public boolean visit(AndExp e) {
  	if (error)
  		return false;
    return e.a.accept(this) && e.b.accept(this);
  }
  
  public boolean visit(OrExp e) {
  	if (error)
  		return false;
    return e.a.accept(this) || e.b.accept(this);
  }
  
  public boolean visit(NotExp e) {
  	if (error)
  		return false;
    return !(e.a.accept(this));
  }
  
  public boolean visit(BNum e) {
  	if (error)
  		return false;
    return e.b;
  }
}

class SymTable {
  int size;
  int n = -1;
  Hashtable[] table;
  
  public SymTable(int s) {
    size = s;
    table = new Hashtable[s];
  }

  public int beginScope() {
  	if (++n >= table.length)
  		return -1;
  	table[n] = new Hashtable();
  	return 0;
  }
  
  public void endScope() {
  	--n;
  }

  public void put(String id, Object obj) {
  	if (obj == null)
  		return ;
    table[n].put(id, obj);
  }
  
  public Object get(String id) {
    return table[n].get(id);
  }
}

