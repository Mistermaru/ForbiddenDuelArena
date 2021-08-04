package com.forbiddenduelarena;

import java.util.ArrayList;
import java.util.Collections;

public class AntiStakeQuoteManager {
    ArrayList<String> quoteList;

    public AntiStakeQuoteManager()
    {
        quoteList = new ArrayList<String>();
        quoteList.add("Stop staking you freaking donkey!");
        quoteList.add("So you wanna get cleaned again? Don't think so buddy.");
        quoteList.add("You look like an idiot coming here to this place again...");
        quoteList.add("Why? Just why? Go back PVMing you filthy gambler!");
        quoteList.add("Keep coming back to this place and u gonna end up like a hobo.");
    }

    public String getRandomQuote()
    {
        Collections.shuffle(quoteList);
        return quoteList.get(0);
    }
}
