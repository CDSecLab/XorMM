package Scheme;

import Client.Xor_Hash;
import Client.entity.KV;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;


public class Test_Xor_Success {
    public static KV[] kv_list;

    public static void main(String args[]) throws Exception {
        int rounds = 1;
        int beta = 18;

        //initialize an database
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("KV_LIST_10_5.dat"));
            kv_list = (KV[]) in.readObject();
            in.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        //setup phase
        Xor_Hash xor = new Xor_Hash(beta);
        int[] times = new int[10];
        for(int i=0;i<rounds;i++) {
            xor.XorMM_Success(kv_list,10);
            times[xor.Get_Try_Times()]++;
        }
        for(int j=0;j<times.length;j++){
            System.out.println("Try "+j+" : "+times[j]);
        }
    }
}
