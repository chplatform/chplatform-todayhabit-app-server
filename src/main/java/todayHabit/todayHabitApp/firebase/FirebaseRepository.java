package todayHabit.todayHabitApp.firebase;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.cloud.FirestoreClient;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import todayHabit.todayHabitApp.domain.member.Member;
import todayHabit.todayHabitApp.dto.member.MemberDto;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FirebaseRepository {
    public void initialize() {
        try{
            ClassPathResource resource = new ClassPathResource("/config/bookinghabit-firebase-adminsdk-kv62g-5323c82991.json");
            FirebaseApp firebaseApp = null;
            List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
            if(firebaseApps != null && !firebaseApps.isEmpty()){
                for(FirebaseApp app : firebaseApps){
                    if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
                        firebaseApp = app;
                    }
                }
            } else{
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                        .setDatabaseUrl("https://bookinghabit.firebaseio.com")
                        .build();

                FirebaseApp.initializeApp(options);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static final String collectionName = "userAuthData";
    public void saveMember(Member member) throws Exception {
        MemberDto memberDto = new MemberDto(member.getName(),member.getEmail(), member.getPasswd() ,member.getPhone());
        Firestore firestore = FirestoreClient.getFirestore();
        System.out.println("emial"+member.getId());
        ApiFuture<WriteResult> apiFuture = firestore.collection(collectionName).document(member.getId().toString())
                .set(memberDto);
    }

    public void updateMemberGymId(Member member, String encodingPasswd) {
        Firestore firestore = FirestoreClient.getFirestore();
        MemberDto memberDto = new MemberDto(member.getName(), member.getEmail(), encodingPasswd, member.getPhone());
        firestore.collection(collectionName).document(member.getId().toString()).set(memberDto);
    }

    public String verifyToken(String idToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String uid = decodedToken.getUid();
        return uid;
    }

}
