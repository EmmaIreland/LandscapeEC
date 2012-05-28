/*
 Sutherland - A flexible, extensible framework for Evolutionary Computation

 Copyright (C) 1998-2000
 Nicholas Freitag McPhee, 
 Mitchell L. Reierson,
 Nicholas J. Hopper,
 Erik Hadden, and
 Jesse Alama

 For further information regarding Sutherland please contact:

 Nic McPhee
 mcphee@mrs.umn.edu
 http://www.mrs.umn.edu/~mcphee/Sutherland
 Division of Science and Mathematics
 University of Minnesota, Morris
 Morris, MN 56267

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Library General Public
 License (Version 2) as published by the Free Software Foundation.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Library General Public License for more details.

 You should have received a copy of the GNU Library General Public
 License along with this library; if not, write to the
 Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 Boston, MA  02111-1307, USA.
 */

package landscapeEC.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import landscapeEC.problem.Individual;

/**
 * A simple class for keeping track of frequency counts on some collection of
 * <CODE>Objects</CODE>.
 * 
 * @author Nic McPhee
 * @version $Revision: 1.3 $
 */
public class FrequencyCounter<T> implements Iterable<T> {
    private HashMap<T,Integer> _map = new HashMap<T,Integer>();

    /**
     * Add a single instance of this <CODE>item</CODE> to the count.
     * 
     * <P>
     * This is final to prevent it being changed in subclasses. If you want to
     * change the behavior of <CODE>addItem</CODE> you should override the other
     * <CODE>addItem</CODE> method (<CODE>addItem(Object item, int count)</CODE>
     * ). This method calls that method with a value of 1 for the
     * <CODE>count</CODE>, so changes made to the other method will be reflected
     * here.
     * 
     * @param item
     *            the item to increment the count for
     */
    public final void addItem(T item) {
        addItem(item, 1);
    }

    /**
     * Add the specified <CODE>item</CODE> <CODE>count</CODE> times.
     * 
     * @param item
     *            the item to increase the count for
     * @param count
     *            the amount to increment the count for this itme by
     */
    public void addItem(T item, int count) {
        _map.put(item, getCount(item) + count);
    }

    /**
     * Find out how many time the specified <CODE>item</CODE> has been counted.
     * 
     * @param item
     *            the item to get the count for
     * @return the number of times we've counted <CODE>item</CODE>
     */
    public int getCount(T item) {
        Integer count = _map.get(item);
        if (count == null) {
            return 0;
        }
        return count;
    }

    /**
     * Return the total count for this counter, i.e., the sum of the count of
     * all of its entries.
     * 
     * @return the total count for this counter
     */
    public int totalCount() {
        int result = 0;
        for (T key : this) {
            result += getCount(key);
        }
        return result;
    }

    /**
     * Return the iterator over the set of all the keys in the count, i.e., all
     * the objects that we've seen and thus have positive counts.
     * 
     * @return an iterator over the set of keys.
     */
    public Iterator<T> keys() {
        return (_map.keySet().iterator());
    }

    /**
     * Return the number of keys (entries) in this counter.
     * 
     * @return the number of keys (entries) in this counter
     */
    public int numKeys() {
        return _map.size();
    }

    /**
     * @return a string listing all the items and their counts.
     */
    @Override
    public String toString() {
        TreeMap<T,Integer> orderedMap = new TreeMap<T,Integer>(
                new Comparator<T>() {
                    public int compare(T a, T b) {
                        return (a.toString().compareTo(b.toString()));
                    }
                });
        orderedMap.putAll(_map);
        return (orderedMap.toString());
    }

    public Iterator<T> iterator() {
        return keys();
    }

    public void reset() {
        _map.clear();
    }

	public void addCounter(FrequencyCounter join) {
		Iterator iter = join.keys();
		while(iter.hasNext()){
			T next = (T)iter.next();
			this.addItem(next, join.getCount(next));
		}
	}

}