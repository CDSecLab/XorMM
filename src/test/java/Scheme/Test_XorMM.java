package Scheme;

import Client.entity.KV;
import Server.server;
import util.AESUtil;
import util.GGM;
import util.Hash;
import Client.Xor_Hash;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class Test_XorMM {

    public static KV[] kv_list;

    public static void main(String[] args) throws Exception {
        //maximum volume length
        int MAX_VOLUME_LENGTH = (int) Math.pow(2, 5);
        int XOR_LEVEL = (int) Math.ceil(Math.log(MAX_VOLUME_LENGTH) / Math.log(3.0));//GGM Tree level for xor hash

        //data size
        int power_size = 10;
        int ELEMENT_SIZE = (int) Math.pow(2, power_size);

        //storage size
        int beta = 0;//parameter for xor hash
        int STORAGE_XOR = (int) Math.floor(((ELEMENT_SIZE * 1.23) + beta) / 3);

        //Search key
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

        System.out.println("---------------------XorMM scheme(our scheme)---------------------");
        //setup phase
        Xor_Hash xor = new Xor_Hash(beta);
        xor.XorMM_setup(kv_list, XOR_LEVEL);

        long K_d = xor.Get_K_d();
        int K_e = xor.Get_K_e();

        byte[][] xor_EMM = xor.Get_EMM();


        //query phase
        server xor_server = new server(xor_EMM,MAX_VOLUME_LENGTH, XOR_LEVEL, STORAGE_XOR);//server receives ciphertext

        System.out.println("\nClient is generating token ... keywords:" + (search_key));
        byte[] tk_key = Hash.Get_SHA_256((search_key+K_d).getBytes(StandardCharsets.UTF_8));//search token

        System.out.println("\nServer is searching and then Client decrypts ... ");
        xor_server.Query_Xor(tk_key);//search
        ArrayList<byte[]> C_key = xor_server.Get_C_key();//client receives results
        byte[] K = Hash.Get_Sha_128((K_e+search_key).getBytes(StandardCharsets.UTF_8));

        for (int i = 0; i < C_key.size(); i++)//decryption
        {
            byte[] str_0 = AESUtil.decrypt(K,C_key.get(i));
            if(str_0!=null){
                String s = new String(str_0);
                System.out.println("Result:" + s);
            }
        }
        xor_server.Store_Server("XorMM");
    }
}
