package Scheme;

import Client.Cuckoo_Hash;
import Client.entity.KV;
import Server.server;
import util.AESUtil;
import util.Hash;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Test_dprfMM {
    public static KV[] kv_list;

    public static void main(String[] args) throws Exception {
        //maximum volume length
        int MAX_VOLUME_LENGTH = (int) Math.pow(2, 5);
        int CUCKOO_LEVEL = (int) Math.ceil(Math.log(MAX_VOLUME_LENGTH) / Math.log(2.0));//GGM Tree level for cuckoo hash

        //data size
        int power_size = 10;
        int ELEMENT_SIZE = (int) Math.pow(2, power_size);

        //storage size for dprfMM
        double alpha = 0.3;//parameter for dprfMM
        int STORAGE_CUCKOO = (int) Math.floor((ELEMENT_SIZE * (1 + alpha)));

        //Searched key
        String search_key = "key_s_1";


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

        System.out.println("---------------------cuckoo hash scheme(dprfMM CCS'19)---------------------");
        //setup phase
        Cuckoo_Hash cuckoo = new Cuckoo_Hash();
        cuckoo.setup(kv_list, CUCKOO_LEVEL);

        ArrayList<KV> stash = cuckoo.Get_Stash();//Get stash
        long K_d = cuckoo.Get_K_d();
        byte[] K_e = cuckoo.Get_K_e();

        byte[][] cuckoo_EMM = cuckoo.Get_EMM();//Get ciphertext

        //query phase
        server cuckoo_server = new server(cuckoo_EMM,MAX_VOLUME_LENGTH, CUCKOO_LEVEL, STORAGE_CUCKOO);//server receives ciphertext
        System.out.println("\nClient is generating token ... keywords:" + (search_key));
        byte[] tk_key = Hash.Get_SHA_256((search_key + K_d).getBytes(StandardCharsets.UTF_8));//search token

        System.out.println("\nServer is searching and then Client decrypts ... ");
        cuckoo_server.Query_Cuckoo(tk_key);
        ArrayList<byte[]> C_key = cuckoo_server.Get_C_key();
        for (int i = 0; i < C_key.size(); ) {
            String s0 = new String(AESUtil.decrypt(K_e, C_key.get(i)));
            String[] s0_list = s0.split(",");
            if(s0_list[0].equals(search_key)){
                System.out.println("Result:" + s0_list[1]);
            }
            String s1 = new String(AESUtil.decrypt(K_e, C_key.get(i + 1)));
            String[] s1_list = s1.split(",");
            if(s1_list[0].equals(search_key)){
                System.out.println("Result:" + s1_list[1]);
            }
            i = i + 2;
        }
        for (int i = 0; i < stash.size(); i++) {
            KV res = stash.get(i);
            if (res.key.equals(search_key))
                System.out.println("Stash Result:" + res);
        }

        cuckoo_server.Store_Server("DprfMM");
    }
}
