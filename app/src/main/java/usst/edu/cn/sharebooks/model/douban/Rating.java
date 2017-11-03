package usst.edu.cn.sharebooks.model.douban;


import java.io.Serializable;

/**
 * 如果这个类不序列化就会报错
 */
public class Rating implements Serializable{
    public int max;
    public int numRaters;
    public String average;
    public int min;
}
