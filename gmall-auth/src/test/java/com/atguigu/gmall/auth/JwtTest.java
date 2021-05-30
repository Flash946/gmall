package com.atguigu.gmall.auth;

import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.common.utils.RsaUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    // 别忘了创建D:\\project\rsa目录
	private static final String pubKeyPath = "D:\\project\\rsa\\rsa.pub";
    private static final String priKeyPath = "D:\\project\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    /**
     * 生成公钥私钥
     * 2021年5月28日22:22:58
     * @throws Exception
     */
    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @BeforeEach
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    /**
     * 生成token
     * @throws Exception
     */
    @Test
    public void testGenerateToken() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "11");
        map.put("username", "liuyan");
        // 生成token
        String token = JwtUtils.generateToken(map, privateKey, 5);
        System.out.println("token = " + token);
    }

    /**
     * token
     * @throws Exception
     */
    @Test
    public void testParseToken() throws Exception {

        String token =" eyJhbGciOiJSUzI1NiJ9.eyJpZCI6IjExIiwidXNlcm5hbWUiOiJsaXV5YW4iLCJleHAiOjE2MjIyMTE4MTB9.W7TmD-YJk3qNKw3lUWgEBIbUIOTm4siwz1q38FemqRIzjy1yvEPqpwT1ByeMVW9vPOSZkvbAcHtGOhLRWEgqEkGwUwgjJIIgD57VRZP522-_b9W9aaijKUCcs-CHFFVUZddpAK2n4Qp_abxFS-cHZZJxqOgE4UjdRhj7FjqwArudERT3sqNWl8sQaRYWWLTvJLrtjAEz4PW29LOKpRhn_TqsQ9aAWQvkc84jLo92bLugja251GP_hrUvC_YVsGsuwYGSirr6wSI9meLtp6wlx2Gs7HT-IOv-wCeTiB6R2AGYRE1uOGCgIaqiEavy8W_3Hpgrhhj0Nuc2hvpO6s3ozw";


        // 解析token
        Map<String, Object> map = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + map.get("id"));
        System.out.println("userName: " + map.get("username"));
    }
}