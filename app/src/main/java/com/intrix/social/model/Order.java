package com.intrix.social.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yarolegovich on 20.12.2015.
 */
public class Order{

    public final int itemId;
    private int amount;
    private List<Integer> customizations;
    private boolean orderConfirmed;
    private List<Integer> customizationsConfirmed;
    private String special;

    public Order(int itemId) {
        this.itemId = itemId;
        this.amount = 1;
        customizations = new ArrayList<>();
        customizationsConfirmed = new ArrayList<>();
    }

    public List<Integer> getCustomizations() {
        return new ArrayList<>(customizations);
    }

    public boolean addCustomization(int customizationId) {
        if (count(customizations, customizationId) < amount) {
            customizations.add(customizationId);
            return true;
        }
        return false;
    }

    public int getCustomizationNumber(int custId) {
        return count(customizations, custId);
    }

    public void removeCustomization(int customizationId) {
        customizations.remove(customizations.indexOf(customizationId));
    }

    public boolean hasCustomization(int customizationId) {
        return customizations.contains(customizations.indexOf(customizationId));
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void incrementAmount() { amount++; }
    public void decrementAmount() { amount--; }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof Order && ((Order) o).itemId == itemId;
    }

    public int getAmount() {
        return amount;
    }

    private static int count(List<Integer> ints, int searchFor) {
        int num = 0;
        for (Integer i : ints) {
            if (i == searchFor) {
                num++;
            }
        }
        return num;
    }

    public boolean isOrderConfirmed() {
        return orderConfirmed;
    }

    public void setOrderConfirmed(boolean orderConfirmed) {
        this.orderConfirmed = orderConfirmed;
    }

    public List<Integer> getCustomizationsConfirmed() {
        return  new ArrayList<>(customizationsConfirmed);
    }

    public void setCustomizationsConfirmed(List<Integer> customizationsConfirmed) {
        this.customizationsConfirmed = customizationsConfirmed;
    }

    public boolean addCustomizationConfirmed(int customizationId) {

        if (count(customizationsConfirmed, customizationId) < amount) {
            customizationsConfirmed.add(customizationId);
            return true;
        }
        return false;
    }

    public List<Integer> getUnconfirmedCustomizations() {
        List<Integer> custms = new ArrayList<Integer>(customizations);
        if(customizationsConfirmed != null)
        for(int i = 0; i < customizationsConfirmed.size(); i++)
        {
            custms.remove(customizationsConfirmed.get(i));
        }
        return custms;
    }

    public int getCustomizationsUnconfirmedCount(int customizationId)
    {
       return count(getUnconfirmedCustomizations(), customizationId);
    }

    public void setSpecial(String specialObj)
    {
        special = specialObj;
    }

    public String getSpecial()
    {
        return special;
    }

    public int getItemId() {
        return itemId;
    }
}
