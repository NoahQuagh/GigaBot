package botdiscord.gigabot.utils.DB;

class equipe_DBTest {

    @org.junit.jupiter.api.Test
    void teamNameExiste_test1() {
        assertTrue(teamNameExiste("NINGEN"));
    }

    @org.junit.jupiter.api.Test
    void teamNameExiste_test2() {
        assertFalse(teamNameExiste("test"));
    }



    @org.junit.jupiter.api.Test
    void estDejaCapitaine_test1() {
        assertTrue(estDejaCapitaine("734686205903372340"));
    }

    @org.junit.jupiter.api.Test
    void estDejaCapitaine_test2() {
        assertFalse(estDejaCapitaine("734686205903372350"));
    }



    @org.junit.jupiter.api.Test
    void estDejaDansUneEquipe_test1() {
        assertTrue(estDejaDansUneEquipe("734686205903372350"));
    }

    @org.junit.jupiter.api.Test
    void estDejaDansUneEquipe_test2() {
        assertFalse(estDejaDansUneEquipe("734686205903378350"));
    }

    @org.junit.jupiter.api.Test
    void estDejaDansUneEquipe_test3() {
        assertTrue(estDejaDansUneEquipe("1212351403604058114"));
    }



    @org.junit.jupiter.api.Test
    void getTeamIdByTeamName_test() {
        assertEquals(getTeamIdByTeamName("NINGEN"),1);
    }
}