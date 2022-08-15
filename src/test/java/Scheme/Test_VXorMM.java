package Scheme;



import Client.Xor_Hash;
import Client.entity.KV;
import util.*;
import Server.server_proof;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class Test_VXorMM {
    public static KV[] kv_list;

    public static void main(String[] args) throws Exception {
        //maximum volume length
        int MAX_VOLUME_LENGTH = (int) Math.pow(2, 5);
        int xor_level = (int) Math.ceil(Math.log(MAX_VOLUME_LENGTH) / Math.log(3.0));//GGM Tree level for xor hash

        //data size
        int power_size = 10;
        int ELEMENT_SIZE = (int) Math.pow(2, power_size);

        //storage size
        int beta = 0;//parameter for xor hash
        int Storage_Xor = (int) Math.floor(((ELEMENT_SIZE* 1.23) + beta) / 3);

        String search_key = "key_s_1";//Search keyword

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



        System.out.println("---------------------VXorMM scheme(our verifiable scheme)---------------------");
        //setup phase
        Xor_Hash vxor = new Xor_Hash(beta);

        xor_hom.initial();
        vxor.VXorMM_Setup(kv_list, xor_level);
        GGM.clear();vxor.Leave_Map_Clear();//clear cache

        long K_d = vxor.Get_K_d();
        int K_e = vxor.Get_K_e();
        int K_p = vxor.Get_K_p();
        int K_m = vxor.Get_K_m();

        byte[][] EMM = vxor.Get_EMM();
        byte[][] VMM = vxor.Get_VMM();

        //query phase
        server_proof vxor_server = new server_proof(EMM,VMM,MAX_VOLUME_LENGTH, xor_level, Storage_Xor);
        long total_time_verify = 0, total_time_query = 0;

        System.out.println("\nClient is generating token ... keywords:" + search_key );
        byte[] tk = Hash.Get_SHA_256((search_key+K_d).getBytes(StandardCharsets.UTF_8));//search token

        System.out.println("\nServer is searching and then Client decrypts ... ");
        vxor_server.Query(tk);//searching
        ArrayList<byte[]> C_key = vxor_server.Get_C_key();//client receives results
        byte[] proof = vxor_server.Get_P_key();
        byte[] K = Hash.Get_Sha_128((K_e+search_key).getBytes(StandardCharsets.UTF_8));


        for (int i = 0; i < C_key.size(); i++){//decryption
            byte[] D_bits = AESUtil.decrypt(K,C_key.get(i));
            if(D_bits!=null) {
                String s = new String(D_bits);
                System.out.println("Result:" + s);
            }
        }
        System.out.println("\nQuery Time of VXorMM:" + (total_time_query) + "ms");

        //verify phase
        System.out.println("\nClient is verifying ... ");
        for (int i = 0; i < MAX_VOLUME_LENGTH; i++) {
            byte[] father_Node = GGM.Tri_GGM_Path(tk, xor_level, tool.TtS(i, 3, xor_level));
            int t0 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 1 , 10),Storage_Xor,0);
            int t1 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 11, 20),Storage_Xor,1);
            int t2 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 21, 30),Storage_Xor,2);
            byte[] res = C_key.get(i);
            byte[] ss = tool.Xor(xor_hom.Gen_Proof(res, K_p), Hash.Get_Sha_128((K_m+","+t0).getBytes(StandardCharsets.UTF_8)));
            ss = tool.Xor(ss, Hash.Get_Sha_128((K_m+","+t1).getBytes(StandardCharsets.UTF_8)));
            ss = tool.Xor(ss, Hash.Get_Sha_128((K_m+","+t2).getBytes(StandardCharsets.UTF_8)));
            proof = tool.Xor(Hash.Get_SHA_256(ss), proof);
        }
        if (tool.Xor_Empty(proof)) {
            System.out.println("Proof is True");
        }else {
            System.out.println("Proof is Wrong");
        }
        vxor_server.Store_Server_Proof("VXorMM");
    }
}
