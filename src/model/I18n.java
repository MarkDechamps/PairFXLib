package model;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import model.Model;

/**
 *
 * @author Mark Dechamps
 */
public class I18n {
 public static String get(String key) {
        return Model.getInstance().translate(key);
    }
}
