package com.tatusafety.matuba.navigation;

import java.util.ArrayList;

/**
 * FragmentHistory
 *
 * Created by Kilasi on 24/11/2018
 */

public class FragmentHistory {
    private ArrayList<Integer> stackArr;

    /**
     * Constructor to create stack with size
     */
    public FragmentHistory() {
        stackArr = new ArrayList<>();
    }

    /**
     * This method adds new entry to the top of the stack
     *
     * @param entry entry
     */
    public void push(int entry) {
        if (isAlreadyExists(entry)) {
            return;
        }
        stackArr.add(entry);

    }

    private boolean isAlreadyExists(int entry) {
        return (stackArr.contains(entry));
    }

    /**
     * This method removes an entry from the top of the stack.
     *
     * @return int
     */
    public int pop() {
        int entry = -1;
        if(!isEmpty()){
            entry = stackArr.get(stackArr.size() - 1);
            stackArr.remove(stackArr.size() - 1);
        }
        return entry;
    }


    /**
     * This method removes an entry from the top of the stack.
     *
     * @param popPreviousAmount if parameter is given, use 2 or more
     * @return int
     */
    public int pop(int popPreviousAmount) {
        int entry = -1;
        if(!isEmpty()){
            entry = stackArr.get(stackArr.size() - popPreviousAmount);
            stackArr.remove(stackArr.size() - popPreviousAmount);
        }
        return entry;
    }



    /**
     * This method returns top of the stack
     * without removing it.
     * @return int
     */
    public int peek() {
        if(!isEmpty()){
            return stackArr.get(stackArr.size() - 1);
        }
        return -1;
    }

    public boolean isEmpty(){
        return (stackArr.size() == 0);
    }

    public int getStackSize(){
        return stackArr.size();
    }

    public void emptyStack() { stackArr.clear(); }
}

