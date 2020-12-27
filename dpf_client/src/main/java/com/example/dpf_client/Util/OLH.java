package com.example.dpf_client.Util;

import java.util.Random;

public class OLH {

    public static int hashDomain(double epsilon){
        int g= (int) Math.pow(Math.E, epsilon)+1;
        return g;
    }
    public static double p_value(double epsilon,int n){
        double p=(Math.pow(Math.E, epsilon))/(Math.pow(Math.E, epsilon)+n-1);
        return p;
    }

    public static int hash(String key,double epsilon ,int g){
        int hash=key.hashCode()%g;
        return hash;
    }

    public static int grr(double p,int key,int g){
        double q=(1-p)/(g-1);
        int perturbed_k=key;
        Random rand=new Random();
        double r=rand.nextDouble();
        if(r>p-q){
            perturbed_k=rand.nextInt(g);
        }
        return perturbed_k;
    }
}
