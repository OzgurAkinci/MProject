public class SimplifyVisitor implements Visitor  {
  public void visit(Stm s) { }
  public void visit(LStm s) { }
  public void visit(AStm s) { }
  public void visit(PStm s) { }

  public Object visit(Exp e) {
    return e.accept(this);
  }
  public Object visit(Header e) {
  	return new Num(0);
  }
  public Object visit(Plus e) {
    Exp a = (Exp)(e.a.accept(this));
    Exp b = (Exp)(e.b.accept(this));
    if (a instanceof Num && ((Num)a).n == 0.0)
      a = null;
    if (b instanceof Num && ((Num)b).n == 0.0)
      b = null;
    if (a == null && b == null)
      return new Num(0);
    if (a == null)
      return b;
    if (b == null)
      return a;
    if (a instanceof Num && ((Num)a).n < 0)
      return new Minus(b, new Num(-1*((Num)a).n));
    if (b instanceof Num && ((Num)b).n < 0)
      return new Minus(a, new Num(-1*((Num)b).n));
    return new Plus(a, b);
  }

  public Object visit(Minus e) {
    Exp a = (Exp)(e.a.accept(this));
    Exp b = (Exp)(e.b.accept(this));
    if (a instanceof Num && ((Num)a).n == 0.0)
      a = null;
    if (b instanceof Num && ((Num)b).n == 0.0)
      b = null;
    if (a == null && b == null)
      return new Num(0);
    if (a == null)
    {
      if (b instanceof Num)
      	return new Num(-1*((Num)b).n);
      else
        return new Times(new Num(-1), b);
    }
    if (b == null)
      return a;
    if (a instanceof Num && b instanceof Num)
      return new Num(((Num)a).n-((Num)b).n);
    if (b instanceof Num && ((Num)b).n < 0)
      return new Plus(a, new Num(-1*((Num)b).n));
    return new Minus(a, b);
  }

  public Object visit(Times e) {
    Exp a = (Exp)(e.a.accept(this));
    Exp b = (Exp)(e.b.accept(this));
    if (a instanceof Num) {
      if (((Num)a).n == 0.0)
        return new Num(0);
      else if (((Num)a).n == 1.0)
        a = null;
    }
    if (b instanceof Num) {
      if (((Num)b).n == 0.0)
        return new Num(0);
      else if (((Num)b).n == 1.0)
        b = null;
    }
    if (a == null && b == null)
      return new Num(1);
    if (a == null)
      return b;
    if (b == null)
      return a;
    if (a instanceof Num && b instanceof Num)
      return new Num(((Num)a).n*((Num)b).n);
    if (a instanceof Num) {
      if (b instanceof Times) {
        if (((Times)b).a instanceof Num)
          return new Times(new Num(((Num)a).n*((Num)((Times)b).a).n), ((Times)b).b);
        else if (((Times)b).b instanceof Num)
          return new Times(new Num(((Num)a).n*((Num)((Times)b).b).n), ((Times)b).a);
      }
    }
    if (b instanceof Num) {
      if (a instanceof Times) {
        if (((Times)a).a instanceof Num)
          return new Times(new Num(((Num)b).n*((Num)((Times)a).a).n), ((Times)a).b);
        else if (((Times)a).b instanceof Num)
          return new Times(new Num(((Num)a).n*((Num)((Times)a).b).n), ((Times)a).a);
      }
    }
    return new Times(a, b);
  }

  public Object visit(Divide e) {
    Exp a = (Exp)(e.a.accept(this));
    Exp b = (Exp)(e.b.accept(this));
    if (a instanceof Num) {
      if (((Num)a).n == 0.0)
        return new Num(0);
    }
    if (b instanceof Num) {
      if (((Num)b).n == 0.0)
        ;//throw new Exception("divide by zero");
      else if (((Num)b).n == 1.0)
        return a;
    }
    return new Divide(a, b);
  }
  
  public Object visit(Mod e) { return new Num(0); }

  public Object visit(Power e) {
    Exp a = (Exp)(e.a.accept(this));
    Exp b = (Exp)(e.b.accept(this));
    if (a instanceof Num) {
      if (((Num)a).n == 0.0)
        return new Num(0);
      else if (((Num)a).n == 1.0)
        return new Num(1);
    }
    if (b instanceof Num) {
      if (((Num)b).n == 0.0)
        return new Num(1);
      else if (((Num)b).n == 1.0)
        return a;
    }
    if (a instanceof Num) {
      if (b instanceof Times) {
        if (((Times)b).a instanceof Num)
          return new Power(new Num(((Num)a).n*((Num)((Times)b).a).n), ((Times)b).b);
      }
    }

    return new Power(a, b);
  }

  public Object visit(Var e) {
    return e;
  }

  public Object visit(Num e) {
    return e;
  }
	
  public Object visit(DNum e) {
	return e;
  }

  public Object visit(Str e) {
	return e;
  }
  
  public Object visit(Ln e) {
    Exp a = (Exp)(e.a.accept(this));
    if (a instanceof Num) {
      if (((Num)a).n == 0.0)
        ;//throw new Exception("divide by zero");
      else if (((Num)a).n == 1.0)
        return new Num(0);
    }
    return new Ln(a);
  }

  public Object visit(Log e) {
    Exp a = (Exp)(e.a.accept(this));
    if (a instanceof Num) {
      if (((Num)a).n == 0.0)
        ;//throw new Exception("divide by zero");
      else if (((Num)a).n == 1.0)
        return new Num(0);
    }
    return new Log(a);
  }
  
  public Object visit(Ep e) {
    Exp a = (Exp)(e.a.accept(this));
    if (a instanceof Num && ((Num)a).n == 0.0)
        return new Num(1);
    return new Ep(a);
  }

  public Object visit(Sin e) {
    Exp a = (Exp)(e.a.accept(this));
    if (a instanceof Num && ((Num)a).n == 0.0)
        return new Num(0);
    return new Sin(a);
  }

  public Object visit(Cos e) {
    Exp a = (Exp)(e.a.accept(this));
    if (a instanceof Num && ((Num)a).n == 0.0)
        return new Num(1);
    return new Cos(a);
  }

  public Object visit(Tan e) {
    Exp a = (Exp)(e.a.accept(this));
    if (a instanceof Num && ((Num)a).n == 0.0)
        ;//throw new Exception("divide by zero");
    return new Tan(a);
  }

  public Object visit(Abs e)  {
   	Exp a = (Exp)(e.a.accept(this));
   	if (a instanceof Num && ((Num)a).n == 0.0)
        return new Num(0);
    return new Abs(a);
  }
  
  public Object visit(Sqrt e) {
    Exp a = (Exp)(e.a.accept(this));
    if (a instanceof Num && ((Num)a).n == 0.0)
    	return new Num(0);
    return new Sqrt(a);
  }
  
  public Object visit(Round e) {
   	Exp a = (Exp)(e.a.accept(this));
   	if (a instanceof Num && ((Num)a).n == 0.0)
        return new Num(0);
    return new Round(a, e.n);
  }
  
  public Object visit(DrvExp e) {
	Exp a = (Exp)(e.e.accept(this));
	return new DrvExp(a, e.n, e.id);
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

