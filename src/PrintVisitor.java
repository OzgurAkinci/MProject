public class PrintVisitor implements Visitor {
	public void visit(Stm s) { }
    public void visit(LStm s) { }
    public void visit(AStm s) { }
    public void visit(PStm s) { }
    public Object visit(Header e) { return e.id; }
    
	public Object visit(Exp e) {
		return e.accept(this);
	}
	public Object visit(Plus e) {
		String a, b;
		/*if (e.a instanceof Num || e.a instanceof Var)
			a = (String)e.a.accept(this);
		else
			a = "(" + e.a.accept(this) + ")";
		if (e.b instanceof Num || e.b instanceof Var)
			b = (String)e.b.accept(this);
		else
			b = "(" + e.b.accept(this) + ")";
		*/
		a = (String)e.a.accept(this);
		b = (String)e.b.accept(this);
		return a + "+" + b;
	}
	public Object visit(Minus e) {
		String a, b;
		/*if (e.a instanceof Num || e.a instanceof Var)
			a = (String)e.a.accept(this);
		else
			a = "(" + e.a.accept(this) + ")";
		*/
		a = (String)e.a.accept(this);
		if (e.b instanceof Num || e.b instanceof DNum || e.b instanceof Str || e.b instanceof Var)
			b = (String)e.b.accept(this);
		else
			b = "(" + e.b.accept(this) + ")";
		return a + "-" + b;
	}
	public Object visit(Times e) {
		String a, b;
		//if (!(e.a instanceof Plus) && !(e.a instanceof Minus))
		if (e.a instanceof Num || e.a instanceof DNum || e.a instanceof Str || e.a instanceof Var)
			a = (String)e.a.accept(this);
		else
			a = "(" + e.a.accept(this) + ")";
		//if (!(e.b instanceof Plus) && !(e.b instanceof Minus))
		if (e.b instanceof Num ||e.b instanceof DNum || e.b instanceof Str ||  e.b instanceof Var)
			b = (String)e.b.accept(this);
		else
			b = "(" + e.b.accept(this) + ")";
		return a + "*" + b;
	}
	public Object visit(Divide e) {
		String a, b;
		//if (!(e.a instanceof Plus) && !(e.a instanceof Minus))
		if (e.a instanceof Num || e.a instanceof DNum || e.a instanceof Str || e.a instanceof Var)
			a = (String)e.a.accept(this);
		else
			a = "(" + e.a.accept(this) + ")";
		//if (!(e.b instanceof Plus) && !(e.b instanceof Minus))
		if (e.b instanceof Num || e.b instanceof DNum || e.b instanceof Str || e.b instanceof Var)
			b = (String)e.b.accept(this);
		else
			b = "(" + e.b.accept(this) + ")";
		return a + "/" + b;
	}
	
	public Object visit(Mod e) {
		return "(" + e.a.accept(this) + "%" + e.b.accept(this) + ")";
	}
	
	public Object visit(Power e) {
		String a, b;
		if (e.a instanceof Num || e.a instanceof DNum || e.a instanceof Str || e.a instanceof Var)
			a = (String)e.a.accept(this);
		else
			a = "(" + e.a.accept(this) + ")";
		if (e.b instanceof Num || e.b instanceof DNum || e.b instanceof Str || e.b instanceof Var)
			b = (String)e.b.accept(this);
		else
			b = "(" + e.b.accept(this) + ")";
		return a + "^" + b;
	}

	public Object visit(Var e) {
		return e.id;
	}
	public Object visit(Num e) {
		return (e.n < 0 ? "(" + e.n + ")" : + e.n + "");
	}
	public Object visit(DNum e) {
		return (e.n < 0 ? "(" + e.n + ")" : + e.n + "");
	}
	public Object visit(Str e) {
		return "\"" + e.s + "\"";
	}
	
	public Object visit(DrvExp e) {
	  String a = (String)(e.e.accept(this));
	  return "drv(" + a + "," + e.n + "," + e.id + ")";
    }
    
    public Object visit(Abs e)  {
    	return "abs(" + e.a.accept(this) + ")";
    }
	public Object visit(Ln e) {
		return "ln(" + e.a.accept(this) + ")";
	}
	public Object visit(Log e) {
		return "log(" + e.a.accept(this) + ")";
    }
	public Object visit(Sin e) {
		return "sin(" + e.a.accept(this) + ")";
	}
	public Object visit(Cos e) {
		return "cos(" + e.a.accept(this) + ")";
	}
	public Object visit(Tan e) {
		return "tan(" + e.a.accept(this) + ")";
	}
	public Object visit(Ep e) {
		return "exp(" + e.a.accept(this) + ")";
	}
	public Object visit(Sqrt e) {
		return "sqrt(" + e.a.accept(this) + ")";
	}
	public Object visit(Round e)  {
    	return "round(" + e.a.accept(this) + ")";
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
