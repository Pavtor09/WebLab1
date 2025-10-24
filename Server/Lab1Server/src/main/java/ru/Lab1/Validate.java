package ru.Lab1;

import java.io.IOException;
import java.math.BigDecimal;

public class Validate {
    public boolean isValid(DataObj obj) throws IOException {
        BigDecimal[] x =  obj.getX();
        BigDecimal y =  obj.getY();
        BigDecimal R =  obj.getR();
        boolean valid = true;
        StringBuilder error  = new StringBuilder();
        for (int i = 0; i < x.length; i++)
        {
            if (x[i].remainder(BigDecimal.valueOf(0.5)).compareTo(BigDecimal.ZERO) != 0 || x[i].abs().compareTo(BigDecimal.valueOf(2)) > 0)
            {
                error.append("x number ").append(i).append(" is not valid |");
                valid = false;
            }
        }
        if (y.abs().compareTo(BigDecimal.valueOf(5)) >= 0)
        {
            error.append("y is not in range of (-5;5) |");
            valid = false;
        }
        if (R.compareTo(BigDecimal.ONE) < 0 || R.compareTo(BigDecimal.valueOf(3)) > 0 || R.remainder(BigDecimal.valueOf(0.5)).compareTo(BigDecimal.ZERO) != 0)
        {
            error.append("R is not valid |");
            valid = false;
        }
        if (!valid)
        {
            JsonError ErrorSend = new JsonError();
            ErrorSend.writeJsonError(422,error.toString());
        }
    return valid;
    }
}
