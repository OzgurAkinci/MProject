public class DeriveVisitor implements Visitor {
  static boolean DEBUG = false;
  boolean error = false;
  String deriveVar = "";
  Program prog;
  SymTable t;

/*  public static void main(String[] args) {
    try {
      Program prog = new Parser(System.in).Start();
      //System.out.println(fL[0] + "");
      DeriveVisitor fp = new DeriveVisitor(prog, new SymTable(10000), "x");
      //fp.visit(new Fn("f", null));
      Exp[] e = { new DNum(1.0), null };
      Exp out = (Exp)fp.visit(new Fn("f", e));
      
      SimplifyVisitor svisitor = new SimplifyVisitor();
      PrintVisitor pvisitor = new PrintVisitor();
      System.out.println("Function: " + (String)pvisitor.visit((Exp)svisitor.visit(out)));
    }
    catch(ParseException ex) {
      System.out.println("Fail!\n" + ex.getMessage());
    }
  }
*/
  
  public DeriveVisitor(Program p, SymTable tb, String var) {
  	prog = p;
  	t = tb;
  	deriveVar = var;
  }
	
  public void visit(Stm s) { }
  public void visit(LStm s) { }
  public void visit(AStm s) { }
  public void visit(PStm s) { }
  
  public Object visit(Exp e) {
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
  	EvalVisitor evisitor = new EvalVisitor(prog, t);
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
      	  	  Object a = evisitor.visit(eqr.e);
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
    Exp a = (Exp)(e.a.accept(this));
    Exp b = (Exp)(e.b.accept(this));
    return new Plus(a, b);
  }

  public Object visit(Minus e) {
    Exp a = (Exp)(e.a.accept(this));
    Exp b = (Exp)(e.b.accept(this));
    return new Minus(a, b);
  }
  
  public Object visit(Times e) {
    Exp a = (Exp)(e.a.accept(this));
    Exp b = (Exp)(e.b.accept(this));
    return new Plus(new Times(a, e.b), new Times(e.a, b));
  }
  public Object visit(Divide e) {
    Exp a = (Exp)(e.a.accept(this));
    Exp b = (Exp)(e.b.accept(this));
    return new Divide(new Minus(new Times(a, e.b), new Times(e.a, b)), new Power(e.b, new Num(2)));
  }

  public Object visit(Mod e) { return new Num(0); }

  public Object visit(Power e) {
    Exp a = (Exp)(e.a.accept(this));
    Exp b = (Exp)(e.b.accept(this));
    return new Plus(new Times(new Times(b, e), new Ln(e.a)), new Times(new Times(e.b, a), new Power(e.a, new Minus(e.b, new Num(1)))));
  }
	
  public Object visit(Var e) {
	return (e.id.equals(deriveVar) ? new Num(1) : new Num(0));
  }

  public Object visit(Num e) {
	return new Num(0);
  }
	
  public Object visit(DNum e) {
    return new DNum(0);
  }
	
  public Object visit(Str e) {
	return new Num(0);
  }
  
  public Object visit(Ln e) {
	Exp a = (Exp)(e.a.accept(this));
	return new Divide(a, e);
  }
  
  public Object visit(Log e) {
    Exp a = (Exp)(e.a.accept(this));
    return new Divide(a, new Times(e.a, new Ln(new Num(10))));
  }
  
  public Object visit(Ep e) {
	Exp a = (Exp)(e.a.accept(this));
	return new Times(a, e);
  }
  
  public Object visit(Sin e) {
	Exp a = (Exp)(e.a.accept(this));
    return new Times(a, new Cos(e.a));
  }
  
  public Object visit(Cos e) {
	Exp a = (Exp)(e.a.accept(this));
	return new Times(new Times(new Num(-1), a), new Sin(e.a));
  }
  
  public Object visit(Tan e) {
	Exp a = (Exp)(e.a.accept(this));
	return new Divide(a, new Power(new Cos(e.a), new Num(2)));
  }

  public Object visit(Abs e)  {
   	Exp a = (Exp)(e.a.accept(this));
   	return new Abs(a);
  }
  
  public Object visit(Sqrt e) {
	Exp a = (Exp)(e.a.accept(this));
	return new Divide(a, new Times(new Num(2), e));
  }
  
  public Object visit(Round e) {
  	Exp a = (Exp)(e.a.accept(this));
   	return new Round(a, e.n);
  }
  
  public Object visit(DrvExp e) {
	return e.accept(this);
  }
  
  public boolean visit(BExp e) { return true; }
  public boolean visit(EQExp e) { return true; }
  public boolean visit(NEExp e) { return true; }
  public boolean visit(LEExp e) { return true; }
  public boolean visit(LTExp e) { return true; }
  public boolean visit(GEExp e) { return true; }
  public boolean visit(GTExp e) { return true; }
  public boolean visit(AndExp e) { return true; }
  public boolean visit(OrExp e) { return true; }
  public boolean visit(NotExp e) { return true; }
  public boolean visit(BNum e) { return true; }
}
