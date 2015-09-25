package br.com.javace.javou.util;

/**
 * Created by Rudsonlive on 10/07/15.
 */
public class Constant {
    public static String TAG = "Javou";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE = "javou.db";
    public static final String NAME_FILE = "/javou.csv";

    public static String POSITION = "POSITION";
    public static String FIRST_RUN = "FIRST_RUN";
    public static String PREFERENCES_APP = "JAVOU";
    public static String ACTION_MODE = "ACTION_MODE";
    public static String PARTICIPANT = "Participant";

    //Tablet participant
    public static final String TABLE_PARTICIPANT = "participant";
    public static final String PARTICIPANT_id = "id";
    public static final String PARTICIPANT_name = "name";
    public static final String PARTICIPANT_phone = "phone";
    public static final String PARTICIPANT_email = "email";
    public static final String PARTICIPANT_photo = "photo";
    public static final String PARTICIPANT_shirtSize = "shirtSize";
    public static final String PARTICIPANT_attend = "attend";
    public static final String PARTICIPANT_nameEvent = "nameEvent";
    public static final String PARTICIPANT_birthDate = "birthDate";
    public static final String PARTICIPANT_sex = "sex";
    public static final String PARTICIPANT_raffled = "raffled";
    public static final String PARTICIPANT_company = "company";
    public static final String PARTICIPANT_code = "code";

    public static String[] CREATE_TABLE() {
        return new String[] {CREATE_PARTICIPANT};
    }

    public static final String PATH_FILE_JAVOU = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + NAME_FILE;

    public static final String PARTICIPANT_COLS[] = {
            PARTICIPANT_id,
            PARTICIPANT_name,
            PARTICIPANT_phone,
            PARTICIPANT_email,
            PARTICIPANT_photo,
            PARTICIPANT_shirtSize,
            PARTICIPANT_attend,
            PARTICIPANT_nameEvent,
            PARTICIPANT_birthDate,
            PARTICIPANT_sex,
            PARTICIPANT_raffled,
            PARTICIPANT_company,
            PARTICIPANT_code
    };

    public static final String FILE_COLS[] = {
            "CODIGO", "NOME", "EMAIL", "CELULAR", "SEXO", "EMPRESA"
    };

    public static String CREATE_PARTICIPANT = " CREATE TABLE participant ( "
            + " id INTEGER CONSTRAINT 'PK_PARTICIPANT' PRIMARY KEY AUTOINCREMENT, "
            + " code INT NULL DEFAULT 0, "
            + " name VARCHAR(100) NOT NULL, "
            + " phone VARCHAR(100) NOT NULL, "
            + " email VARCHAR(250) NULL, "
            + " photo TEXT NULL, "
            + " shirtSize INT NULL DEFAULT 0, "
            + " attend INT NULL DEFAULT 0, "
            + " nameEvent VARCHAR(100) NULL, "
            + " birthDate VARCHAR(10) NULL, "
            + " sex CHAR(1) NULL, "
            + " raffled INT NULL DEFAULT 0, "
            + " company VARCHAR(250) NULL ); ";
}
