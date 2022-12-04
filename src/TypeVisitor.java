public class TypeVisitor implements Visitor  {
  boolean error = false;
  Program prog;
  SymTable t;
  
  public TypeVisitor(Program p, SymTable tb) {
  	prog = p;
  	t = tb;
  }

  public void visit(Stm s) {
    s.accept(this);
  }

  public void visit(LStm s) {
    s.a.accept(this);
    //if (!error)
    s.b.accept(this);
  }

  public void visit(AStm s) {
  	Object a = s.e.accept(this);
  	if (a != null) {
      Object b = t.get(s.id);
      if (b == null)
        t.put(s.id, a);
      else if ((a instanceof Num && !(b instanceof Num)) ||
      	       (a instanceof DNum && !(b instanceof DNum)) ||
      	       (a instanceof Str && !(b instanceof Str)))
        System.out.println("Consistency error: " + s.id + "=" + new PrintVisitor().visit(s.e));
    }
    else
      System.out.println("Inference error: " + s.id + "=" + new PrintVisitor().visit(s.e));
  }

  public void visit(PStm s) {
    EList eq = s.eq;
  	if (eq != null) {
      while (eq != null) {
        Object a = eq.e.accept(this);
        if (a == null) {
      	  System.out.println("Type error: " + new PrintVisitor().visit(eq.e));
  	  	  return;
        }
        eq = eq.eq;
      }
  	}
  }

  public Object visit(Exp e) {
    return e.accept(this);
  }
  
  public Object visit(Header fr) {
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
      	  	  if (a == null)
      	  	  	return null;
      	  	  if (eql.e instanceof Plus) {
      	  	  	args[argi] = ((Var)((Plus)(eql.e)).a).id;
      	  	  	if (a instanceof Num)
      	  	  	  argn[argi++] = new Num(0);
      	  	  	else if (a instanceof DNum)
      	  	  	  argn[argi++] = new DNum(0);
      	  	  	else
      	  	  	  argn[argi++] = new Str("");;
      	  	  }
      	  	  else if (eql.e instanceof Var) {
      	  	  	args[argi] = ((Var)(eql.e)).id;
      	  	  	argn[argi++] = a;
      	  	  }
      	  	  else {//if (eql.e instanceof Num)
      	  	    args[argi] = "";  //formal parametre bir deger olarak girilmisse
      	  	  	argn[argi++] = a;
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
      	  for (int j = 0; j < argi; j++) {
      	  	if (args[j].equals(""))
      	  		continue;
      	  	if (argn[j] instanceof Num) {
      	  	  t.put(args[j], argn[j]);
      	  	}
      	  	else if (argn[j] instanceof DNum) {
      	  	  t.put(args[j], argn[j]);
      	    }
      	    else {
      	  	  t.put(args[j], argn[j]);
      	    }
      	  }
      	  if (p.def.s != null) {
      	    //this.visit(p.def.s);
      	    p.def.s.accept(this);
      	    if (error)
      	  	  return null;
      	  }
      	  
      	  QList eq = p.def.eq;
      	  while (eq != null) {
      	    if (eq.b.accept(this)) {
      	      //res = this.visit(eq.e);
      	      res = eq.e.accept(this);
      	      break;
      	    }
      	    eq = eq.eq;
      	  }
      	  t.endScope();
      	  break;
      	}
      }
      p = p.prog;
  	}
  	
  	return (error ? null : res);
  }

  public Object visit(Plus e) {
  	Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a == null || b == null)
		return null;
  	if (a instanceof Str || b instanceof Str)
	  return new Str("");
  	else if (a instanceof DNum || b instanceof DNum)
  	  return new DNum(0);
  	else
  	  return new Num(0);
  }

  public Object visit(Minus e) {
  	Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a == null || b == null)
		return null;
  	if (a instanceof Str || b instanceof Str) {
  	  System.out.println("Type error: " + new PrintVisitor().visit(e));
  	  error = true;
	  return null;
  	}
  	else if (a instanceof DNum || b instanceof DNum)
  	  return new DNum(0);
  	else
  	  return new Num(0);
  }

  public Object visit(Times e) {
  	Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a == null || b == null)
		return null;
  	if (a instanceof Str || b instanceof Str) {
  	  System.out.println("Type error: " + new PrintVisitor().visit(e));
  	  error = true;
	  return null;
  	}
  	else if (a instanceof DNum || b instanceof DNum)
  	  return new DNum(0);
  	else
  	  return new Num(0);
  }

  public Object visit(Divide e) {
  	Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a == null || b == null)
		return null;
  	if (a instanceof Str || b instanceof Str) {
  	  System.out.println("Type error: " + new PrintVisitor().visit(e));
  	  error = true;
	  return null;
  	}
  	else if (a instanceof DNum || b instanceof DNum)
  	  return new DNum(0);
  	else
  	  return new Num(0);
  }

  public Object visit(Mod e) {
  	Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a == null || b == null)
		return null;
  	if (a instanceof Str || b instanceof Str) {
  	  System.out.println("Type error: " + new PrintVisitor().visit(e));
  	  error = true;
	  return null;
  	}
  	else if (a instanceof DNum || b instanceof DNum)
  	  return new DNum(0);
  	else
  	  return new Num(0);
  }

  public Object visit(Power e) {
  	Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a == null || b == null)
		return null;
  	if (a instanceof Str || b instanceof Str) {
  	  System.out.println("Type error: " + new PrintVisitor().visit(e));
  	  error = true;
	  return null;
  	}
  	else if (a instanceof DNum || b instanceof DNum)
  	  return new DNum(0);
  	else
  	  return new Num(0);
  }
  
  public Object visit(Var e) {
/*  	Object a = t.get(e.id);
  	if (a instanceof Num)
	  return new Num(0);
	else if (a instanceof DNum)
	  return new DNum(0);
	else if (a instanceof Str)
      return new Str("");
    else
      return null;*/
    return t.get(e.id);
  }

  public Object visit(Num e) {
    return new Num(0);
  }
  
  public Object visit(DNum e) {
    return new DNum(0);
  }
  
  public Object visit(Str e) {
    return new Str("");
  }
 
  public Object visit(Ln e) {
	Object a = e.a.accept(this);
	if (a == null)
		return false;
	if (a instanceof Num)
	  return new Num(0);
	else if (a instanceof DNum)
	  return new DNum(0);
	else
	{
	  System.out.println("Type error: " + new PrintVisitor().visit(e));
	  error = true;
	  return null;
	}
  }
  
  public Object visit(Log e) {
	Object a = e.a.accept(this);
	if (a == null)
		return false;
	if (a instanceof Num)
	  return new Num(0);
	else if (a instanceof DNum)
	  return new DNum(0);
	else
	{
	  System.out.println("Type error: " + new PrintVisitor().visit(e));
	  error = true;
	  return null;
	}
  }
		  
  public Object visit(Sin e) {
	Object a = e.a.accept(this);
	if (a == null)
		return false;
	if (a instanceof Num)
	  return new Num(0);
	else if (a instanceof DNum)
	  return new DNum(0);
	else
	{
	  System.out.println("Type error: " + new PrintVisitor().visit(e));
	  error = true;
	  return null;
	}
  }
		  
  public Object visit(Cos e) {
	Object a = e.a.accept(this);
	if (a == null)
		return false;
	if (a instanceof Num)
	  return new Num(0);
	else if (a instanceof DNum)
	  return new DNum(0);
	else
	{
	  System.out.println("Type error: " + new PrintVisitor().visit(e));
	  error = true;
	  return null;
	}
  }
		  
  public Object visit(Tan e) {
	Object a = e.a.accept(this);
	if (a == null)
		return false;
	if (a instanceof Num)
	  return new Num(0);
	else if (a instanceof DNum)
	  return new DNum(0);
	else
	{
	  System.out.println("Type error: " + new PrintVisitor().visit(e));
	  error = true;
	  return null;
	}
  }
		  
  public Object visit(Ep e) {
	Object a = e.a.accept(this);
	if (a == null)
		return false;
	if (a instanceof Num)
	  return new Num(0);
	else if (a instanceof DNum)
	  return new DNum(0);
	else
	{
	  System.out.println("Type error: " + new PrintVisitor().visit(e));
	  error = true;
	  return null;
	}
  }

  public Object visit(Abs e) {
  	Object a = e.a.accept(this);
	if (a == null)
		return false;
	if (a instanceof Num)
	  return new Num(0);
	else if (a instanceof DNum)
	  return new DNum(0);
	else
	{
	  System.out.println("Type error: " + new PrintVisitor().visit(e));
	  error = true;
	  return null;
	}
  }

  public Object visit(Sqrt e) {
	Object a = e.a.accept(this);
	if (a == null)
		return null;
	if (a instanceof Num)
	  return new Num(0);
	else if (a instanceof DNum)
	  return new DNum(0);
	else
	{
	  System.out.println("Type error: " + new PrintVisitor().visit(e));
	  error = true;
	  return null;
	}
  }
    
  public Object visit(Round e) {
  	return new Num(0);
  }
    
  public Object visit(DrvExp e) {
  	return new DNum(0);
  }
  
  public boolean visit(BExp e) {
    return e.accept(this);
  }
  
  public boolean visit(EQExp e) {
  	Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a == null || b == null)
		return false;
  	if (a instanceof Str || b instanceof Str) {
  	  if (a instanceof Num || a instanceof DNum)
  	    return false;
  	  if (b instanceof Num || b instanceof DNum)
  	  	return false;
  	  return true;
  	}
  	
  	return true;
  }
  
  public boolean visit(NEExp e) {
  	Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a == null || b == null)
		return false;
  	if (a instanceof Str || b instanceof Str) {
  	  if (a instanceof Num || a instanceof DNum)
  	    return false;
  	  if (b instanceof Num || b instanceof DNum)
  	  	return false;
  	  return true;
  	}
  	
  	return true;
  }
  
  public boolean visit(LEExp e) {
  	Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a == null || b == null)
		return false;
  	if (a instanceof Str || b instanceof Str) {
  	  if (a instanceof Num || a instanceof DNum)
  	    return false;
  	  if (b instanceof Num || b instanceof DNum)
  	  	return false;
  	  return true;
  	}
  	
  	return true;
  }
  
  public boolean visit(LTExp e) {
  	Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a == null || b == null)
		return false;
  	if (a instanceof Str || b instanceof Str) {
  	  if (a instanceof Num || a instanceof DNum)
  	    return false;
  	  if (b instanceof Num || b instanceof DNum)
  	  	return false;
  	  return true;
  	}
  	
  	return true;
  }

  public boolean visit(GTExp e) {
    Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a == null || b == null)
		return false;
  	if (a instanceof Str || b instanceof Str) {
  	  if (a instanceof Num || a instanceof DNum)
  	    return false;
  	  if (b instanceof Num || b instanceof DNum)
  	  	return false;
  	  return true;
  	}
  	
  	return true;
  }
  
  public boolean visit(GEExp e) {
  	Object a = e.a.accept(this);
  	Object b = e.b.accept(this);
  	if (a == null || b == null)
		return false;
  	if (a instanceof Str || b instanceof Str) {
  	  if (a instanceof Num || a instanceof DNum)
  	    return false;
  	  if (b instanceof Num || b instanceof DNum)
  	  	return false;
  	  return true;
  	}
  	
  	return true;
  }

  public boolean visit(AndExp e) {
    return e.a.accept(this) && e.b.accept(this);
  }
  
  public boolean visit(OrExp e) {
    return e.a.accept(this) && e.b.accept(this);
  }
  
  public boolean visit(NotExp e) {
    return e.a.accept(this);
  }
  
  public boolean visit(BNum e) {
    return true;
  }
}
