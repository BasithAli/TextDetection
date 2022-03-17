package ntu.n1043826.scanit.Helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DataFire {
    private String userID;
    private String key;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;
    private DatabaseReference dbref;
    private DatabaseReference documentsRef,documentsPDFRef,QrRef;
    String email,name,imgurl;
    public DataFire() {

    }
    public DatabaseReference getDbref(){
        dbref =  FirebaseDatabase.getInstance().getReference();
        return dbref;
    }

    public FirebaseAuth getmAuth(){
        mAuth = FirebaseAuth.getInstance();
        return mAuth;
    }
    public DatabaseReference getDocumentsRef(){
        documentsRef = FirebaseDatabase.getInstance().getReference().child("Documents");

        documentsRef.keepSynced(true);
        return documentsRef;
    }

    public DatabaseReference getDocumentsPdfRef(){
        documentsPDFRef = FirebaseDatabase.getInstance().getReference().child("Documents").child("PDF");

        documentsPDFRef.keepSynced(true);
        return documentsPDFRef;
    }
    public DatabaseReference getQRDataRef(){
        QrRef = FirebaseDatabase.getInstance().getReference().child("Documents").child("QRCODE");

        QrRef.keepSynced(true);
        return QrRef;
    }
    public String getUserID(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        return userID;
    }

    public FirebaseUser getCurrentUser(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        return user;
    }

    public String getCurrentUserName(){
        name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        return name;
    }
    public String getCurrentUserEmail(){
        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        return email;
    }
    public String getCurrentUserProfile(){
        imgurl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
        return imgurl;
    }


    public StorageReference getStorageref(){
        storageReference = FirebaseStorage.getInstance().getReference("Documents");
        return storageReference;
    }

    public String getKey(){
        documentsRef = FirebaseDatabase.getInstance().getReference().child("Documents");
        key = documentsRef.push().getKey();;
        return key;
    }
}
