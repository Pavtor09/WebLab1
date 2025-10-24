package ru.Lab1;

import java.math.BigDecimal;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataObj {
    @JsonProperty("x")
    private BigDecimal[] x;
    @JsonProperty("y")
    private BigDecimal y;
    @JsonProperty("R")
    private BigDecimal R;
    @JsonProperty("Time")
    private LocalTime time;
    @JsonProperty("Ex_Time")
    private long executionTime;
    @JsonProperty("Hit")
    private boolean[] hit;

    public boolean CheckNull()
    {
        return this.x == null || this.y == null || this.R == null;
    }

    public void SetNowTime()
    {
        this.time = LocalTime.now();
    }
    public void SetExTime(long time)
    {
        this.executionTime = time;
    }
    public BigDecimal[] getX()
    {
        return this.x;
    }
    public BigDecimal getY()
    {
        return this.y;
    }
    @JsonProperty("R")
    public BigDecimal getR()
    {
        return this.R;
    }
    public void CalculateHit()
    {
        hit = new boolean[x.length];
        for (int i = 0; i < x.length; i++) {
            // проверка на правый верхний круг
            if (x[i].compareTo(BigDecimal.ZERO) > 0 && y.compareTo(BigDecimal.ZERO) >= 0) {
                hit[i] =  x[i].multiply(x[i]).add(y.multiply(y)).compareTo(R.multiply(R)) <= 0;
            }
            // проверка на левый верхний треугольник
            else if(x[i].compareTo(BigDecimal.ZERO) <= 0 && y.compareTo(BigDecimal.ZERO) > 0)
            {
                hit[i] = R.pow(2).divide(new BigDecimal("2")).negate().add(R.multiply(y).divide(new BigDecimal("2"))).subtract(R.multiply(x[i])).compareTo(BigDecimal.ZERO) <= 0;
            }
            //проверка на левый нижний прямоугольник
            else if (x[i].compareTo(BigDecimal.ZERO) < 0 && y.compareTo(BigDecimal.ZERO) <= 0)
            {
             hit[i] = x[i].compareTo(R.negate()) >= 0 && y.compareTo(R.divide(new BigDecimal("2")).negate()) >= 0;
            }
        }
    }
}
