/*
 * Copyright 1999-2011 Alibaba Group.
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
package com.alibaba.dubbo.common.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.alibaba.dubbo.common.model.Person;
import com.alibaba.dubbo.common.model.SerializablePerson;
import com.alibaba.dubbo.common.model.person.BigPerson;
import com.alibaba.dubbo.common.model.person.FullAddress;
import com.alibaba.dubbo.common.model.person.PersonInfo;
import com.alibaba.dubbo.common.model.person.PersonStatus;
import com.alibaba.dubbo.common.model.person.Phone;

/**
 * @author ding.lid
 */
public class PojoUtilsTest {

    public void assertObject(Object data) {
        Object generalize = PojoUtils.generalize(data);
        Object realize = PojoUtils.realize(generalize, data.getClass());
        assertEquals(data, realize);
    }
    
    public <T> void assertArrayObject(T[] data) {
        Object generalize = PojoUtils.generalize(data);
        @SuppressWarnings("unchecked")
        T[] realize = (T[]) PojoUtils.realize(generalize, data.getClass());
        assertArrayEquals(data, realize);
    }

    @Test
    public void test_primitive() throws Exception {
        assertObject(Boolean.TRUE);
        assertObject(Boolean.FALSE);

        assertObject(Byte.valueOf((byte) 78));

        assertObject('a');
        assertObject('中');

        assertObject(Short.valueOf((short) 37));

        assertObject(78);

        assertObject(123456789L);

        assertObject(3.14F);
        assertObject(3.14D);
    }

    @Test
    public void test_pojo() throws Exception {
        assertObject(new Person());
        assertObject(new SerializablePerson());
    }

    @Test
    public void test_PrimitiveArray() throws Exception {
        assertObject(new boolean[] { true, false });
        assertObject(new Boolean[] { true, false, true });

        assertObject(new byte[] { 1, 12, 28, 78 });
        assertObject(new Byte[] { 1, 12, 28, 78 });

        assertObject(new char[] { 'a', '中', '无' });
        assertObject(new Character[] { 'a', '中', '无' });

        assertObject(new short[] { 37, 39, 12 });
        assertObject(new Short[] { 37, 39, 12 });

        assertObject(new int[] { 37, -39, 12456 });
        assertObject(new Integer[] { 37, -39, 12456 });
        
        assertObject(new long[] { 37L, -39L, 123456789L });
        assertObject(new Long[] { 37L, -39L, 123456789L });

        assertObject(new float[] { 37F, -3.14F, 123456.7F });
        assertObject(new Float[] { 37F, -39F, 123456.7F });
        
        assertObject(new double[] { 37D, -3.14D, 123456.7D });
        assertObject(new Double[] { 37D, -39D, 123456.7D});
        

        assertArrayObject(new Boolean[] { true, false, true });

        assertArrayObject(new Byte[] { 1, 12, 28, 78 });

        assertArrayObject(new Character[] { 'a', '中', '无' });

        assertArrayObject(new Short[] { 37, 39, 12 });

        assertArrayObject(new Integer[] { 37, -39, 12456 });
        
        assertArrayObject(new Long[] { 37L, -39L, 123456789L });

        assertArrayObject(new Float[] { 37F, -39F, 123456.7F });
        
        assertArrayObject(new Double[] { 37D, -39D, 123456.7D});
    }

    @Test
    public void test_PojoArray() throws Exception {
        Person[] array = new Person[2];
        array[0] = new Person();
        {
            Person person = new Person();
            person.setName("xxxx");
            array[1] = person;
        }
        assertArrayObject(array);
    }

    // FIXME
    @Ignore("Type missing, Person -> Map")
    @Test
    public void test_simpleCollection() throws Exception {
        List<Person> list = new ArrayList<Person>();
        list.add(new Person());
        {
            Person person = new Person();
            person.setName("xxxx");
            list.add(person);
        }
        assertObject(list);
    }

    BigPerson bigPerson;
    {
        bigPerson = new BigPerson();
        bigPerson.setPersonId("id1");
        bigPerson.setLoginName("name1");
        bigPerson.setStatus(PersonStatus.ENABLED);
        bigPerson.setEmail("abc@123.com");
        bigPerson.setPenName("pname");

        ArrayList<Phone> phones = new ArrayList<Phone>();
        Phone phone1 = new Phone("86", "0571", "11223344", "001");
        Phone phone2 = new Phone("86", "0571", "11223344", "002");
        phones.add(phone1);
        phones.add(phone2);

        PersonInfo pi = new PersonInfo();
        pi.setPhones(phones);
        Phone fax = new Phone("86", "0571", "11223344", null);
        pi.setFax(fax);
        FullAddress addr = new FullAddress("CN", "zj", "1234", "Road1", "333444");
        pi.setFullAddress(addr);
        pi.setMobileNo("1122334455");
        pi.setMale(true);
        pi.setDepartment("b2b");
        pi.setHomepageUrl("www.abc.com");
        pi.setJobTitle("dev");
        pi.setName("name2");

        bigPerson.setInfoProfile(pi);
    }

    // FIXME
    @Ignore("Type missing, List<Phone> -> List<Map>")
    @Test
    public void test_total() throws Exception {
        Object generalize = PojoUtils.generalize(bigPerson);
        Object realize = PojoUtils.realize(generalize, BigPerson.class);
        assertEquals(bigPerson, realize);
    }

    // FIXME
    @Ignore("Type missing, List<Phone> -> List<Map>") 
    @Test
    public void test_total_Array() throws Exception {
        Object[] persons = new Object[] { bigPerson, bigPerson, bigPerson };

        Object generalize = PojoUtils.generalize(persons);
        Object[] realize = (Object[]) PojoUtils.realize(generalize, Object[].class);
        assertArrayEquals(persons, realize);
    }

    public static <T extends Comparable<T>> T min(T[] arr) {
        if (arr == null || arr.length == 0)
            return null;

        T smallest = arr[0];
        for (int i = 1; i < arr.length; ++i) {
            if (smallest.compareTo(arr[i]) > 0) {
                smallest = arr[i];
            }
        }
        return smallest;
    }

    public static <T extends Comparable<? super T>> T min2(T[] arr) {
        if (arr == null || arr.length == 0)
            return null;

        T smallest = arr[0];
        for (int i = 1; i < arr.length; ++i) {
            if (smallest.compareTo(arr[i]) > 0) {
                smallest = arr[i];
            }
        }
        return smallest;
    }

    public static <T extends Comparable<T> & Serializable> T max(T[] arr) {
        if (arr == null || arr.length == 0)
            return null;

        T biggest = arr[0];
        for (int i = 1; i < arr.length; ++i) {
            if (biggest.compareTo(arr[i]) < 0) {
                biggest = arr[i];
            }
        }
        return biggest;
    }

    public static <T extends Comparable<T> & Serializable> T max2(Comparable<? extends Serializable>[] arr) {
        return null;
    }

}