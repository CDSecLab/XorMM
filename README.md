# XorMM & VXorMM

This repo shows the constructions of dprfMM [1], XorMM and VXorMM. And this application is developed by using JDK 17.0.1.




## The Main Phases

1. Setup: the construction with different dataset;
2. Query: the key is searched on the sever and values will be return to the client;
3. Verify: This part is constructed in verifiable schemes, i.e. VXorMM. The client can verify the search results.

## File Structure

```javascript
XorMM/
├── README.md                                       //  introduction
├── src
│   ├── main
│   │   └── java
│   │      ├── Client
│   │      │    ├── entity                         
│   │      │    │   └── KV.java                     //  entity for key/value pairs
│   │      │    ├── Cuckoo_Hash.java                //  setup for dprfMM
│   │      │    └── Xor_Hash.java                   //  setup for XorMM and VXorMM
│   │      │
│   │      ├── Server
│   │      │    ├── entity                         
│   │      │    │   ├── server_entity.java          //  entity of the server for dprfMM and XorMM
│   │      │    │   └── server_proof_entity.java    //  entity of the server for VXorMM
│   │      │    ├── server.java                     //  the server for dprfMM and XorMM
│   │      │    └── server_proof.java               //  the server for VXorMM
│   │      │
│   │      └── util
│   │           └── *                               // tools
│   │
│   └── test
│       └── java
│           └── Scheme
│               ├── Test_dprfMM                     //  dprfMM scheme
│               ├── Test_GenMM                      //  generate multi-maps
│               ├── Test_VXorMM                     //  VXorMM scheme
│               ├── Test_Xor_Success                //  test the probaility of success for XorMM
│               └── Test_XorMM                      //  XorMM scheme
│ 
├── KV_LIST_10_5.dat                                //  the multi-map consists of 2^10 key/value pairs with maximum volume 2^5
├── Plaintext_LIST_10_5.text                        //  the multi-map consists of 2^10 key/value pairs with maximum volume 2^5
├── Server_DprfMM.dat                               //  server storage in dprfMM
├── Server_VXorMM.dat                               //  server storage in VXorMM
├── Server_XorMM.dat                                //  server storage in XorMM
├── XorMM.iml
└── pom.xml                                         
```

## Contact Us
1. Tianci Li, tcli_134@stu.xidian.edu.cn
2. Jianfeng Wang, jfwang@xidian.edu.cn

## Reference
1. Jianfeng Wang, Shi-Feng Sun, Tianci Li, Saiyu Qi, Xiaofeng Chen: Practical Volume-Hiding Encrypted Multi-Maps with Optimal Overhead and Beyond. CCS 2022: 2825-2839.[Online]. Available: [https://doi.org/10.1145/3319535.3354213](https://dl.acm.org/doi/abs/10.1145/3548606.3559345)

2. S. Patel, G. Persiano, K. Yeo, and M. Yung, “Mitigating leakage in secure cloud-hosted data structures: Volume-hiding for multi-maps via hashing,” in CCS 2019, London, UK, November 11-15, 2019. ACM, 2019, pp. 79–93. [Online]. Available:
https://doi.org/10.1145/3319535.3354213

3. T. M. Graf and D. Lemire, “Xor filters: Faster and smaller than bloom and cuckoo filters,” CoRR, 2019. [Online]. Available: http://arxiv.org/abs/1912.08258
