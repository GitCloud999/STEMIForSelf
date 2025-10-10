package com.stemi.dataClasses;

public class CardiacStruct {
    CardiacData lead0CardiacStruct = new CardiacData();
    CardiacData lead1CardiacStruct = new CardiacData();
    CardiacData lead2CardiacStruct = new CardiacData();
    CardiacData lead3CardiacStruct = new CardiacData();
    CardiacData lead4CardiacStruct = new CardiacData();
    CardiacData lead5CardiacStruct = new CardiacData();
    CardiacData lead6CardiacStruct = new CardiacData();
    CardiacData lead7CardiacStruct = new CardiacData();
    CardiacData lead8CardiacStruct = new CardiacData();

    public void setLead0CardiacStruct(CardiacData lead0CardiacStruct) {
        this.lead0CardiacStruct = lead0CardiacStruct;
    }

    public void setLead1CardiacStruct(CardiacData lead1CardiacStruct) {
        this.lead1CardiacStruct = lead1CardiacStruct;
    }

    public void setLead2CardiacStruct(CardiacData lead2CardiacStruct) {
        this.lead2CardiacStruct = lead2CardiacStruct;
    }

    public void setLead3CardiacStruct(CardiacData lead3CardiacStruct) {
        this.lead3CardiacStruct = lead3CardiacStruct;
    }

    public void setLead4CardiacStruct(CardiacData lead4CardiacStruct) {
        this.lead4CardiacStruct = lead4CardiacStruct;
    }

    public void setLead5CardiacStruct(CardiacData lead5CardiacStruct) {
        this.lead5CardiacStruct = lead5CardiacStruct;
    }

    public void setLead6CardiacStruct(CardiacData lead6CardiacStruct) {
        this.lead6CardiacStruct = lead6CardiacStruct;
    }

    public void setLead7CardiacStruct(CardiacData lead7CardiacStruct) {
        this.lead7CardiacStruct = lead7CardiacStruct;
    }

    public void setLead8CardiacStruct(CardiacData lead8CardiacStruct) {
        this.lead8CardiacStruct = lead8CardiacStruct;
    }

    public CardiacData getLeadWiseCardiacStructLeadRangeFrom0To8(int lead) {
        if (lead == 0)
            return lead0CardiacStruct;
        if (lead == 1)
            return lead1CardiacStruct;
        if (lead == 2)
            return lead2CardiacStruct;
        if (lead == 3)
            return lead3CardiacStruct;
        if (lead == 4)
            return lead4CardiacStruct;
        if (lead == 5)
            return lead5CardiacStruct;
        if (lead == 6)
            return lead6CardiacStruct;
        if (lead == 7)
            return lead7CardiacStruct;
        if (lead == 8)
            return lead8CardiacStruct;
        return null;
    }

}
