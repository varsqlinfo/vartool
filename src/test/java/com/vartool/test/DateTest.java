package com.vartool.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.junit.Test;

public class DateTest {
	
	public static void main(String[] args) {
		
	
		DateTime currentDate = new DateTime();
		DateTime chkDate = new DateTime();
		chkDate= chkDate.plusDays(1);
		
		System.out.println(new Period(currentDate, chkDate, PeriodType.days()).getDays());
		System.out.println(currentDate.toString("yyyyMMdd"));
		
		System.out.println(chkDate.toString("yyyyMMdd"));
		
	}

}
