package com.wf.equityoption.pricing;

/*
Copyright (C) 2007 Richard Gomes

This source code is release under the BSD License.

This file is part of JQuantLib, a free-software/open-source library
for financial quantitative analysts and developers - http://jquantlib.org/

JQuantLib is free software: you can redistribute it and/or modify it
under the terms of the JQuantLib license.  You should have received a
copy of the license along with this program; if not, please email
<jquant-devel@lists.sourceforge.net>. The license is also available online at
<http://www.jquantlib.org/index.php/LICENSE.TXT>.

This program is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE.  See the license for more details.

JQuantLib is based on QuantLib. http://quantlib.org/
When applicable, the original copyright notice follows this notice.
*/

/*
Copyright (C) 2005, 2006, 2007 StatPro Italia srl

This file is part of QuantLib, a free-software/open-source library
for financial quantitative analysts and developers - http://quantlib.org/

QuantLib is free software: you can redistribute it and/or modify it
under the terms of the QuantLib license.  You should have received a
copy of the license along with this program; if not, please email
<quantlib-dev@lists.sf.net>. The license is also available online at
<http://quantlib.org/license.shtml>.

This program is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE.  See the license for more details.
*/


import org.jquantlib.QL;
import org.jquantlib.Settings;
import org.jquantlib.daycounters.Actual365Fixed;
import org.jquantlib.daycounters.DayCounter;
import org.jquantlib.exercise.AmericanExercise;
import org.jquantlib.exercise.BermudanExercise;
import org.jquantlib.exercise.EuropeanExercise;
import org.jquantlib.exercise.Exercise;
import org.jquantlib.instruments.EuropeanOption;
import org.jquantlib.instruments.Option;
import org.jquantlib.instruments.Payoff;
import org.jquantlib.instruments.PlainVanillaPayoff;
import org.jquantlib.instruments.VanillaOption;
import org.jquantlib.methods.lattices.AdditiveEQPBinomialTree;
import org.jquantlib.methods.lattices.CoxRossRubinstein;
import org.jquantlib.methods.lattices.JarrowRudd;
import org.jquantlib.methods.lattices.Joshi4;
import org.jquantlib.methods.lattices.LeisenReimer;
import org.jquantlib.methods.lattices.Tian;
import org.jquantlib.methods.lattices.Trigeorgis;
import org.jquantlib.pricingengines.AnalyticEuropeanEngine;
import org.jquantlib.pricingengines.vanilla.BaroneAdesiWhaleyApproximationEngine;
import org.jquantlib.pricingengines.vanilla.BinomialVanillaEngine;
import org.jquantlib.pricingengines.vanilla.BjerksundStenslandApproximationEngine;
import org.jquantlib.pricingengines.vanilla.IntegralEngine;
import org.jquantlib.pricingengines.vanilla.JuQuadraticApproximationEngine;
import org.jquantlib.pricingengines.vanilla.finitedifferences.FDAmericanEngine;
import org.jquantlib.pricingengines.vanilla.finitedifferences.FDBermudanEngine;
import org.jquantlib.pricingengines.vanilla.finitedifferences.FDEuropeanEngine;
import org.jquantlib.processes.BlackScholesMertonProcess;
import org.jquantlib.quotes.Handle;
import org.jquantlib.quotes.Quote;
import org.jquantlib.quotes.SimpleQuote;
import org.jquantlib.termstructures.BlackVolTermStructure;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.termstructures.volatilities.BlackConstantVol;
import org.jquantlib.termstructures.yieldcurves.FlatForward;
import org.jquantlib.time.Calendar;
import org.jquantlib.time.Date;
import org.jquantlib.time.Month;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;
import org.jquantlib.time.calendars.Target;


public class EquityOptionBlackScholes implements Runnable {

   public static void main(final String[] args) {
       new EquityOptionBlackScholes().run();
   }

   @Override
   public void run() {

       QL.info("::::: " + this.getClass().getSimpleName() + " :::::");
      
       // set up dates
       final Calendar calendar = new Target();
       final Date todaysDate = new Date(15, Month.May, 1998);
       final Date settlementDate = new Date(17, Month.May, 1998);
       new Settings().setEvaluationDate(todaysDate);

       // our options
       final Option.Type type = Option.Type.Call;
       final double strike = 40.0;
       final double underlying = 36.0;
       /*@Rate*/final double riskFreeRate = 0.06;
       final double volatility = 0.2;
       final double dividendYield = 0.00;


       final Date maturity = new Date(17, Month.May, 1999);
       final DayCounter dayCounter = new Actual365Fixed();

       // define line formatting
       //              "         1         2         3         4         5         6         7         8         9"
       //              "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
       //              "                            Method      European      Bermudan      American";
       //              "================================== ============= ============= ============="
       //              "12345678901234567890123456789012345678901234 123.567890123 123.567890123 123.567890123";
       final String fmt    = "%34s %13.9f %13.9f %13.9f\n";
       final String fmttbd = "%34s %13.9f %13.9f %13.9f  (TO BE DONE)\n";

       // write column headings
       //                 "         1         2         3         4         5         6         7         8"
       //                 "12345678901234567890123456789012345678901234567890123456789012345678901234567890"
       System.out.println("                            Method      European      Bermudan      American");
       System.out.println("================================== ============= ============= =============");

       // Define exercise for European Options
       final Exercise europeanExercise = new EuropeanExercise(maturity);

       // bootstrap the yield/dividend/volatility curves
       final Handle<Quote> underlyingH = new Handle<Quote>(new SimpleQuote(underlying));
       final Handle<YieldTermStructure> flatDividendTS = new Handle<YieldTermStructure>(new FlatForward(settlementDate, dividendYield, dayCounter));
       final Handle<YieldTermStructure> flatTermStructure = new Handle<YieldTermStructure>(new FlatForward(settlementDate, riskFreeRate, dayCounter));
       final Handle<BlackVolTermStructure> flatVolTS = new Handle<BlackVolTermStructure>(new BlackConstantVol(settlementDate, calendar, volatility, dayCounter));
       final Payoff payoff = new PlainVanillaPayoff(type, strike);

       final BlackScholesMertonProcess bsmProcess = new BlackScholesMertonProcess(underlyingH, flatDividendTS, flatTermStructure, flatVolTS);

       // European Options
       final VanillaOption europeanOption = new EuropeanOption(payoff, europeanExercise);
       // Black-Scholes for European
       String method = "Black-Scholes";
       europeanOption.setPricingEngine(new AnalyticEuropeanEngine(bsmProcess));
       System.out.printf(fmt, method, europeanOption.NPV(), Double.NaN, Double.NaN );

   }

}
