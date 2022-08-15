package Scheme;

import Client.entity.KV;
import util.tool;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

public class Test_GenMM {
    public static void main(String[] args) throws Exception{
        //maximum volume length
        int MAX_VOLUME_LENGTH = (int) Math.pow(2, 5);

        //data size
        int power_size = 10;
        int ELEMENT_SIZE = (int) Math.pow(2, power_size);

        //initialize an database
        KV[] kv_list = new KV[ELEMENT_SIZE];
        for (int i = 0; i < MAX_VOLUME_LENGTH; i++) {
            KV tmp =  new KV();
            tmp.key = "key_s_"+0;
            tmp.value = "value_s_"+i;
            tmp.counter = i;
            kv_list[i] = tmp;
        }
        int counter = MAX_VOLUME_LENGTH;
        int key = 1;
        while (counter < ELEMENT_SIZE) {
            Random random = new Random();
            int key_length = random.nextInt(MAX_VOLUME_LENGTH - 1) + 1;
            if (counter + key_length >= ELEMENT_SIZE) {
                key_length = ELEMENT_SIZE - counter;
            }
            int begin = random.nextInt(100);
            for (int j = 0; j < key_length; j++) {
                KV tmp =  new KV();
                tmp.key = "key_s_"+key;
                tmp.counter = j;
                tmp.value = ("value_s_"+(begin+j));
                kv_list[counter + j] = tmp;
            }
            counter = counter + key_length;
            key++;
        }
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("KV_LIST_10_5.dat"));
            out.writeObject(kv_list);
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        tool.WriteDataToFile(kv_list,"Plaintext_10_5.txt");
    }
}
