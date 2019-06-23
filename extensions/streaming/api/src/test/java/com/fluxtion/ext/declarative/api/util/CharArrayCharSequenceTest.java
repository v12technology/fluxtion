/*
 * Copyright 2019 uniVocity Software Pty Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fluxtion.ext.declarative.api.util;

import com.fluxtion.ext.streaming.api.util.CharArrayCharSequence;
import com.fluxtion.ext.streaming.api.util.CharArrayCharSequence.CharSequenceView;
import java.util.HashMap;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Tests for CharArrayCharSequence with shared array and views on top
 * @author gregp
 */
public class CharArrayCharSequenceTest {

    @Test
    public void testOne() {
        CharArrayCharSequence seq = new CharArrayCharSequence("FreddieHiggins".toCharArray());
        CharSequenceView freddie = seq.subSequence(0, 7);
        CharSequenceView higgins = seq.subSequence(7, seq.length());

        assertThat(freddie.toString(), is("Freddie"));
        assertThat(higgins.toString(), is("Higgins"));
        assertFalse(freddie == higgins);

        //convert freddie to higgins - global position shift
        CharSequence freedie2Higgins = freddie.subSequenceNoOffset(7, seq.length());
        assertThat(freedie2Higgins.toString(), is("Higgins"));
        assertTrue(freedie2Higgins == freddie);
        
        //convert to fred
        freddie = seq.subSequence(0, 7);
        CharSequenceView fred = freddie.subSequence(0, 4);
        assertThat(fred.toString(), is("Fred"));
        assertTrue(fred == freddie);
    }

    @Test
    public void testAccess() {
        CharArrayCharSequence seq = new CharArrayCharSequence("eurusd 1.35".toCharArray());
        CharSequence eurusd = seq.subSequence(0, 6);
        CharSequence price = seq.subSequence(7, seq.length());
        assertThat(eurusd.toString(), is("eurusd"));
        assertThat(price.toString(), is("1.35"));

        assertThat(seq.length(), is(11));
        assertThat(eurusd.length(), is(6));
        assertThat(price.length(), is(4));

        assertThat(seq.charAt(7), is('1'));
        assertThat(eurusd.charAt(3), is('u'));
        assertThat(price.charAt(1), is('.'));

        price.subSequence(0, 1).toString();
        
    }

    @Test
    public void testEquals() {
        CharArrayCharSequence seq = new CharArrayCharSequence("eurusd 1.35".toCharArray());
        CharSequence eurusd = seq.subSequence(0, 6);
        CharSequence price = seq.subSequence(7, seq.length());

        assertThat(eurusd.toString(), is("eurusd"));
        assertThat(price.toString(), is("1.35"));

        assertEquals(price, "1.35");
        assertEquals(eurusd, "eurusd");
        assertEquals("1.35", price.toString());
        assertEquals("eurusd", eurusd.toString());
    }

    @Test
    public void testHashCode() {
        CharArrayCharSequence seq = new CharArrayCharSequence("eurusd freusd".toCharArray());
        CharSequence usd1 = seq.subSequence(3, 6);
        CharSequence usd2 = seq.subSequence(10, seq.length());

        assertEquals(usd2, "usd");
        assertEquals(usd1, "usd");
        assertEquals(usd1, usd2);
        assertEquals(usd1.hashCode(), usd2.hashCode());
    }
    
    @Test
    public void keysInMap(){
        HashMap<CharSequence, Integer> seq2Int = new HashMap<CharSequence, Integer>();
        CharArrayCharSequence seq = new CharArrayCharSequence("eurusd freusd".toCharArray());
        CharSequence usd1 = seq.subSequence(3, 6);
        CharSequence usd2 = seq.subSequence(10, seq.length());
        CharSequence eur = seq.subSequence(0, 3);
        
        assertEquals(usd2, "usd");
        assertEquals(usd1, "usd");
        assertEquals(usd1, usd2);
        assertEquals(usd1.hashCode(), usd2.hashCode()); 
        
        assertEquals(eur, "eur");
        assertEquals(eur.hashCode(), "eur".hashCode()); 
        
        seq2Int.put(eur, 100);
        seq2Int.put(usd1, 999);
        
        assertNull(seq2Int.get("eur"));
        assertEquals(100, (int)seq2Int.get(eur));
        assertEquals((long)999, (long)seq2Int.get(usd2));
    }
    
    @Test
    public void trim(){
        CharArrayCharSequence seq = new CharArrayCharSequence("eurusd freusd ".toCharArray());
        CharSequenceView usd1 = seq.subSequence(3, 7);
        CharSequenceView usd2 = seq.subSequence(10, seq.length());
        CharSequenceView eur = seq.subSequence(6, seq.length());
        
        assertThat(usd1.trim(), is("usd"));
        assertThat(usd2.trim(), is("usd"));
        assertThat(eur.trim(), is("freusd"));
        
    }
}
