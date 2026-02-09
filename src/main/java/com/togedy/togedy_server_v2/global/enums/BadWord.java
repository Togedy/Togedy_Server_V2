package com.togedy.togedy_server_v2.global.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BadWord {

    // 욕설
    SIBAL("씨발"),
    SSHIBHAL("씹할"),
    SHORT_SIBAL("ㅅㅂ"),
    DOUBLE_SIBAL("ㅆㅂ"),
    DOUBLE_SIBAL2("ㅆㅃ"),
    JOT("좆"),
    JOT_GAT("좆같"),
    J_GAT("ㅈ같"),
    JONNA("존나"),
    JN("ㅈㄴ"),

    GAE_SAEKKI("개새끼"),
    GAE_XX("개xx"),
    GAE_GATDA("개같다"),
    GAE_NYEON("개년"),

    BYEONGSIN("병신"),
    BEUNGSHIN("븅신"),
    BS("ㅄ"),
    BYEONG1SIN("병1신"),

    MICIN_NOM("미친놈"),
    MICIN_NYEON("미친년"),
    MICHI_NYEON("미치년"),
    MI_X("미X"),

    JIRAL("지랄"),
    JI1RAL("지1랄"),

    SSHIB_SAE("씹새"),
    SSYANG("썅"),
    SSANG_NOM("쌍놈"),

    // 모욕 / 혐오
    EMCHANG("엠창"),
    EMSAENG("엠생"),
    MANGSAENG("망생"),

    GEOJEO("꺼져"),
    DAKCHYEO("닥쳐"),
    IPDAK("입닥"),
    GEOJESSEM("꺼지셈"),
    YEOT_MEOGEO("엿먹어"),
    JUGEO("죽어"),
    DWIJEO("뒤져"),
    BBEO_CUE("뻐큐"),
    FINGER("ㅗ"),

    BOJI("보지"),
    JAJI("자지"),

    GWANJONG("관종"),
    JJINTTA("찐따"),

    HAN_NAM("한남"),
    DOENJANGNYEO("된장녀"),
    KIMCHINYE("김치녀"),

    MUJI("무지"),
    MEONGCHEONG("멍청"),
    JEONEUNG("저능"),
    JEOGIL("저질"),
    SUREGI("쓰레기"),
    PAEGEUB("폐급"),
    NOT_HUMAN("인간아님"),

    // 변형 욕설
    YOUR_PARENTS("너네_부모"),
    YOUR_MOM("네_엄마"),
    KKJ("ㄲㅈ"),
    SI8("시8"),
    BEUNG_DOLLAR("븅$"),
    JIRAL_SPREAD("지ㄹㅏㄹ"),
    SIB_UNDER("ㅅ_ㅂ"),
    JOT_UNDER("ㅈ_같"),
    SIB_DASH("시-발"),
    JGASDA("ㅈ가ㅆ다"),
    SIBAAL("시바알"),
    SII_BAL("시이발"),
    SSWIBAL("쒸발"),

    // 차별 표현
    MENTAL_PATIENT("정신병자"),
    DDORAI("또라이"),
    DORAI("돌아이"),
    MENTAL_RETARD("정신지체"),
    DISABLED("장애인"),

    // 저주
    CANCER("암_걸려라"),
    AIDS("에이즈"),

    // 기타
    HAN_NAM_CHUNG("한남충"),
    GAY_XX("게이xx"),
    LES_XX("레즈xx"),
    HOMO("호모"),
    SEONGGWE("성괴"),
    TRANS("트젠"),

    JEOLLA("전라디언"),
    GYEONGSANG("경상도x"),
    SEOUL_CHON("서울촌놈"),
    JANGKKA("짱깨"),
    JJOKBBARI("쪽바리"),
    BLACK_BRO("흑형"),

    LOCAL_UNIV("지잡대"),
    THIRD_RATE_UNIV("삼류대"),
    LOSER("인생낙오자"),

    OLD_MAN("틀딱"),
    STUDENT_CHUNG("급식충"),
    UNEMPLOYED_CHUNG("백수충"),
    PARTTIME_CHUNG("알바충"),
    NOGADA("노가다"),
    DIRT_SPOON("흙수저"),
    OLD_GUY("개저씨"),
    MOM_CHUNG("맘충");

    private final String word;
    
}
