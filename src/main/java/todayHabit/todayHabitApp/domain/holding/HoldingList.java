package todayHabit.todayHabitApp.domain.holding;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import todayHabit.todayHabitApp.domain.member.MemberOwnMembership;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Table(name = "holdingList")
public class HoldingList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holdingMembership_id")
    private HoldingMembership holdingMembership;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_membership_id")
    private MemberOwnMembership memberOwnMembership;

    private LocalDate startDay;
    private LocalDate endDay;

    @Enumerated(EnumType.STRING)
    private HoldingLocation holdingLocation;

    protected HoldingList() {

    }

    public HoldingList(HoldingMembership holdingMembership, MemberOwnMembership membership, LocalDate startDay, LocalDate endDay, HoldingLocation holdingLocation) {
        this.holdingMembership = holdingMembership;
        this.memberOwnMembership = membership;
        this.startDay = startDay;
        this.endDay = endDay;
        this.holdingLocation = holdingLocation;
    }

}
