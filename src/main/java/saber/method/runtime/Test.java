package saber.method.runtime;

import com.alibaba.fastjson.JSON;

/**
 * Created by baipeng on 2017/2/22.
 */
public class Test {
    private static final class Expression {
        public String fun;
        public  Object[] pars;
    }
    public static int t1(int v1,int v2){
        return v1 + v2;
    }

    public static void main(String args[]){
        Expression e = new Expression();
        e.fun = "t";
        e.pars = new Object[2];
        e.pars[0] = 1;
        e.pars[1] = "a,bc";
        System.out.println(JSON.toJSONString(e));
    }
}
