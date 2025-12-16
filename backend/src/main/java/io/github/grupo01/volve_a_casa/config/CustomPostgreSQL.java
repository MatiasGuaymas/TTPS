package io.github.grupo01.volve_a_casa.config;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class CustomPostgreSQL extends PostgreSQLDialect {
    @Override
    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
        super.initializeFunctionRegistry(functionContributions);
        
        final String EARTH_RADIUS = "6371";

        String haversineSql = 
            " ( " + 
            "  POWER(SIN(RADIANS(?3 - ?1) / 2.0), 2.0) " + 
            "  + COS(RADIANS(?1)) * COS(RADIANS(?3)) " + 
            "  * POWER(SIN(RADIANS(?4 - ?2) / 2.0), 2.0) " + 
            " ) ";

        String distanceSql = 
            " (" + 
            EARTH_RADIUS + " * 2.0 * ATAN2(SQRT(" + haversineSql + "), SQRT(1.0 - " + haversineSql + ")) " +
            " ) ";

        functionContributions.getFunctionRegistry().register("calculate_distance", 
            new StandardSQLFunction("calculate_distance", StandardBasicTypes.DOUBLE)
        );
    }

 }
