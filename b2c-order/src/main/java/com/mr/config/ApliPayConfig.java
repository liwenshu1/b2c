package com.mr.config;

public class ApliPayConfig {

    //应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static final String APP_ID = "2021000118642404";
    //应用私钥
    public static final String MERCHANT_PRIVATE_KEY = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCxs7YG1P4fjzKIHi3oKl06fD6+OhFULzAn04ufv/VLEWWSa7FJAt5CW/kz8F2I4viiVHdvkGUWe40n1vY9fjPTb3iriSNe7C/xF8+P/BPykxeuv8ffd3UzazF3EqC65zornio4yxOG7XbMdfvSmWgk5cU2/z9v6ehM3XZ7fdoawMnaejkN1FAfIyWHbORJwez1tTVZf07j+Yiqxoepvzvx3fw6+WcJusEBB/ayPEYOaXdc0B1q+c0FO1g+TezUHFhX5uI21jh1oSwaFKlbjWoaJrOOjiSeck0n9LKvTz3NzUJi81uWsJMyC613Cm8ynsOWmJqL5Kzvwt9M22AMoJ3rAgMBAAECggEAKfiPOqfd6ahRwBKYtygiYujrjNR8OUz368AsTyvwjK1sNxwWgG8b/cJpZpoUDXvGS9j1aLVLGvdNjKZZ/IYtSXgfoSw/3XCdli0Qiip4r6eMRWrok2BMnztnMH+G1P4+IAU4ZbeZQhoeIAaR1tkC3wwh+25oFk/B3T3FeT9tXznZZbWGg9HmS+P4QUdvM+kmjy1EQCIvO1yUL/0VFssXCT+59kTSVM9YmN7UQYYGzFuJjHmfWV8KBxuZaKier+xLyvsGG3PI21Yo8U6qyC5Qesav+dBiCREPqWf0zJsWcN82KGGL3SFUBAYCGouz+B9B4OLJL0gS+++ClA4Wif3lcQKBgQD+HdQ8PAUoSkEcPy94NmxsVLAkA8vKcs/QotGz63FrRe2f8xRia4aRUf4Hhawk8J+wZeFGJgFkrWPH0+7HxmLP+inlAQvzXDPgRl1dBZidQYxKcghCeJNRdIzpNRnTeeVdDfcVQIYKDqjf3NTiw0bPj1no/q0iCJGf7nZ6TTEtEwKBgQCzBOPWhvLtMHxJ7V2l1sOXetXPX1pAuWgLJPXiYc0MhNy+Bg7qsRAomgce4cVlCKV6BvB/ZniTG/7I9N1pJnuzJZOPAYe2t5Fx39ybjtBrx6o5vJwuCSzRa6rlohn6JcTiHRtMB/w97oMI3glyxSANj7n4e/rCUtViTiinftQeyQKBgQD0CAFAtwLc5jIQxjz33IvSnKFqdJrIF+mqGjH37ipFMio4aF8zBL993pds8dmWv90asTvc+bJs1HX1+HheMQ3DXCHPYWnwkqLexbPzGOSuX7wx4cA81T3kA2Eus2fRxkWVT0vwhL4z+lACazelbJIb0TYuoiPxgvkV9RKh8G9BMQKBgQCPCfA2gHtoU95rf3LdSJ3d0nM2j/zgTIS6qtyg9v5M63gpiVOv6Kx9lJzt6v27Pc5ik1rkoO7ktHC/BxBGrU4XlYWytUlIEKLxNyrhTJpyFAaKvY6ZJhGef4X8l6pj+nu3JASkmpjMr2AZ3Lf18VYsH9Q7nGx/ioofU04ejzNUKQKBgQC74+Ivj+VsY3UuU7MfWtBDoflN7aDrVw8GRB720g5yYz/5jLQrpcK/Wg5b27bC7RVCYnoXL/SaEGzf3a29r+tXzXsidudyBezbc8mBzpU5RGxCtdylJMX3TBVvZTO019BRFo6Y28cp25OgVxyT218hiSZp5h39XMrs+Udo3267YQ==";
    //支付宝公钥
    public static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnENDn/+pwnKsOs147gdYZ6iGFYVvXSZWpqAVuahWwYtY3X/pccC4s0O9O7m0YjSsjeJ7Bkbg5JL+C8UZZwQ1jhelKxTUn1HY0ql+Y72AuxL+OJAKd5QuOjJ8HUBbL8EXpeiJimgsuHnpWhvzTAVwdvv8Lr7oH3SESRjeVCOMMg2DWDNiW0GGdXcqW2WWgabWZGiKjIE17tti1o8IBxvSujfnbjPHVZ5ZXuARmH7BdZhuVeekfQK3rsvBReGyFzjOfEV2/f4IlqjfJbwzF+Q2Tz5GtlngvWAVLpeMiDJYGH6/T4rs8tAqhTHa3tUiSws2+eTTbm2G+y1yYnPWCOLeYwIDAQAB";
    //通知的回调地址 写一个确实支付成功改订单状态等，//必须为公网地址，
    public static final String NOTIFY_URL = "http://127.0.0.1:8089/paySuccess";
    //用户支付完成回调地址//可以写内网地址
    public static final String RETURN_URL = "http://127.0.0.1:8089/callback";
    // 签名方式
    public static String SIGN_TYPE = "RSA2";
    // 字符编码格式
    public static String CHARSET = "gbk";
    // 支付宝网关
    public static String GATEWAYURL = "https://openapi.alipaydev.com/gateway.do";

}