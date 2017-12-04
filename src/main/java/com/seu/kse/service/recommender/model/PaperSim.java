package com.seu.kse.service.recommender.model;



import java.io.Serializable;


/**
 * Created by yaosheng on 2017/5/31.
 */
public class PaperSim implements Comparable<PaperSim>, Serializable {
    public PaperSim(String pid,  double sim){
        this.pid = pid;
        this.sim = sim;
    }
    private String pid;
    private double sim;




    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public double getSim() {
        return sim;
    }

    public void setSim(double sim) {
        this.sim = sim;
    }

    public int compareTo(PaperSim o) {
        return isEqual(this, o) ? 0 : this.getSim() > o.getSim() ? 1 : -1;
    }
    private boolean isEqual(PaperSim o1, PaperSim o2){
        if(Math.abs(o1.getSim() - o2.getSim()) < 0.00001){
            return true;
        }
        return false;
    }
}
