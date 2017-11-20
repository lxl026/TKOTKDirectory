package company.leon.tkotkdirectory;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Leon on 2017/11/18.
 */

public class Hero  extends DataSupport implements Serializable{
    private int id;
    private String name;        //英雄名字
    private String nationality;  //英雄势力，魏蜀吴
    private String birth;       //生卒年月
    private String sex;         //性别
    private String origin;      //籍贯
    private int picture;        //头像
    private String pictureSource; //拍照和选择图片的文件路径
    //无参构造函数
    public Hero(){}
    //有参构造函数
    public Hero(String nationality, String name){
        this.name=name;
        this.nationality=nationality;
    }
    public int getId() {return id;}
    public String getBirth() { return birth;}
    public String getName() {
        return name;
    }
    public String getNationality() {
        return nationality;
    }
    public String getSex() {
        return sex;
    }
    public String getOrigin() {
        return origin;
    }
    public int getPicture() {
        return picture;
    }
    public void setBirth(String birth) {
        this.birth = birth;
    }
    public void setPicture(int picture) {
        this.picture = picture;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    public void setId(int id) {this.id = id;}
    public void setNationality(String nationality){
        this.nationality = nationality;
    }

    public void setPictureSource(String pictureSource) {
        this.pictureSource = pictureSource;
    }

    public String getPictureSource() {
        return pictureSource;
    }
}
