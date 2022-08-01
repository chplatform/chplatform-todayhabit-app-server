package todayHabit.todayHabitApp.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import todayHabit.todayHabitApp.domain.member.Male;
import todayHabit.todayHabitApp.domain.member.Member;
import todayHabit.todayHabitApp.dto.member.LoginMemberDto;
import todayHabit.todayHabitApp.dto.member.MemberOwnMembershipsDto;
import todayHabit.todayHabitApp.error.AlreadyExistMemberException;
import todayHabit.todayHabitApp.repository.member.MemberRepository;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@SpringBootTest
@Transactional
class MemberServiceTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    EntityManager em;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Test
    public void 중복회원_조사() throws Exception{
        //given
        Member member = new Member("회원1", "homelala@naver.com", "19971109", Male.남, "01011112222" ,"1111","sample");

        //when

        //then
        assertThrows(AlreadyExistMemberException.class, () -> {
            memberService.joinMember(member);
        });
    }
    
    @Test
    public void 회원가입() throws Exception{
        //given
        Member member = new Member("회원1", "homa@naver.com", "19971109", Male.남, "01011112222" ,"1111","sample");

        //when
        memberService.joinMember(member);
        em.flush();
        em.clear();

        //then
        assertEquals(member.getId(),memberRepository.findMemberById(member.getId()).getId());
    }
    @Test
    public void 회원센터정보등록() throws Exception{
        //given
        Member member = new Member("회원1", "homa@naver.com", "19971109", Male.남, "01011112222" ,"1111","sample");
        em.persist(member);
        em.flush();
        em.clear();
        //when
        memberService.updateCenterInfo("ft1333", member.getId());
        em.flush();
        em.clear();

        System.out.println(memberRepository.findMemberById(member.getId()).getGym().getSerialNumber());
        //then
        Assert.assertEquals(memberRepository.findMemberById(member.getId()).getGym().getSerialNumber(), "ft1333");
    }

    @Test
    public void 로그인() throws Exception{
        //given

        //when
        long msg = memberService.logIn("test3@naver.com", "ft1333", "2222");
        //then
        Assert.assertEquals(msg,"로그인이 완료되었습니다");
    }

    @Test
    public void 비밀번호변경() throws Exception{
        //given
        String encode = passwordEncoder.encode("1111");
        Member member = new Member("회원1", "tw4@naver.com", "19971109", Male.남, "01011112222",encode,"sample");
        em.persist(member);
        //when
        memberService.updatePasswd("homelala@naver.com" ,"ft1333", "AIzaSyA4xnXye_bngFET6vDOu3OcG2ozOisB_6E");
        em.flush();
        em.clear();
        //then
        Member findMember = memberRepository.findMemberById(member.getId());
        System.out.println(findMember.getEmail());
        Assert.assertEquals(true,passwordEncoder.matches("ft1333", findMember.getPasswd()));
    }

    @Test
    public void 센터정보변경_회원권변경() throws Exception{
        //given
        List<MemberOwnMembershipsDto> memberOwnMembershipsDtos = memberService.changeMemberOwnMembership(193l, 5l);
        //when

        //then
        assertEquals(1, memberOwnMembershipsDtos.size());
    }

    @Test
    public void 회원정보수정() throws Exception{
        //given
        memberService.updateMemberInfo(193l, "이하홉", Male.남, "01011111111");
        //when
        em.flush();
        em.clear();
        //then
        Member memberById = memberRepository.findMemberById(193l);
        assertEquals(memberById.getPhone(),"01011111111");
    }

    @Test
    public void 회원로그아웃() throws Exception{
        //given
        String encode = passwordEncoder.encode("1111");
        Member member = new Member("회원1", "tw4@naver.com", "19971109", Male.남, "01011112222", encode, "sample");
        memberService.joinMember(member);
        em.flush();
        em.clear();
        memberService.logIn("tw4@naver.com", "1111", "234");
        em.flush();
        em.clear();
        //when
        memberService.memberLogout(member.getId());
        //then
        Member memberById = memberRepository.findMemberById(member.getId());
        Assert.assertEquals(memberById.getToken(), null);
    }
}