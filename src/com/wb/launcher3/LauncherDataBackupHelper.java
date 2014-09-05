package com.wb.launcher3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import com.wb.launcher3.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

public class LauncherDataBackupHelper {

    public static final String TAG = "LauncherDataBackupHelper";

    public static final String COMMAND_BACKUP = "backupDatabase";
    public static final String COMMAND_RESTORE = "restroeDatabase";
    public static final String COMMAND_RESET = "resetDatabase";

    public static final String LAUNCHER_DATABASE_NAME = "launcher.db";

    public static final int HAVE_NO_SDCARD = 0;

    public static final int BACKUP_OK = HAVE_NO_SDCARD + 1;
    public static final int BACKUP_FAIL = BACKUP_OK + 1;

    public static final int RESTORE_OK = BACKUP_FAIL + 1;
    public static final int RESTORE_FAIL = RESTORE_OK + 1;

    public static final int RESET_OK = RESTORE_FAIL + 1;
    public static final int RESET_FAIL = RESET_OK + 1;

    private Context mContext = null;
    private BackupTask mBackupTask = null;

    private ProgressDialog dialog = null;

    private OnBackupAndRestoreListener mOnBackupAndRestoreListener = null;
    
    //*/zhangwuba modify 2014-5-8
    public static final String BACKUP_FILE = "Launcherbakup";
    //*/

    public LauncherDataBackupHelper(Context context) {
        mContext = context;
        dialog = new ProgressDialog(mContext);
        dialog.setMessage(mContext.getString(R.string.launcher_settings_operation_tips));
    }

    public void dataRecover() {
        if (mBackupTask != null && !mBackupTask.isCancelled()) {
            mBackupTask.cancel(true);
        }
        mBackupTask = new BackupTask(mContext);
        mBackupTask.execute(COMMAND_RESTORE);
    }

    public void dataBackup() {
        if (mBackupTask != null && !mBackupTask.isCancelled()) {
            mBackupTask.cancel(true);
        }
        mBackupTask = new BackupTask(mContext);
        mBackupTask.execute(COMMAND_BACKUP);
    }

    public void dataReset() {
        if (mBackupTask != null && !mBackupTask.isCancelled()) {
            mBackupTask.cancel(true);
        }
        mBackupTask = new BackupTask(mContext);
        mBackupTask.execute(COMMAND_RESET);
    }

    public void setOnBackupAndRestoreListener(OnBackupAndRestoreListener onBackupAndRestoreListener) {
        mOnBackupAndRestoreListener = onBackupAndRestoreListener;
    }

    public class BackupTask extends AsyncTask<String, Void, Integer> {

        private Context mContext;

        public BackupTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {

            if (!params[0].equals(COMMAND_RESET)
                    && !Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                return HAVE_NO_SDCARD;
            }

            File dataDir = new File(Environment.getDataDirectory() + "/data/" + mContext.getPackageName());
            File dbFile = new File(dataDir, "/databases/" + LAUNCHER_DATABASE_NAME);
            File shareFile = new File(dataDir, "/shared_prefs/launcher_prefs.xml");

            File exportDir = new File(Environment.getExternalStorageDirectory(), BACKUP_FILE);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            File backupDb = new File(exportDir, dbFile.getName());
            File backupShared = new File(exportDir, shareFile.getName());

            String command = params[0];
            if (command.equals(COMMAND_BACKUP)) {
                try {
                    delDirectory(exportDir);
                    backupDb.createNewFile();
                    fileCopy(dbFile, backupDb);
                    if (shareFile.exists()) {
                        backupShared.createNewFile();
                        fileCopy(shareFile, backupShared);
                    }
                    return BACKUP_OK;
                } catch (Exception e) {
                    e.printStackTrace();
                    return BACKUP_FAIL;
                }
            } else if (command.equals(COMMAND_RESTORE)) {
                try {
                    fileCopy(backupDb, dbFile);
                    if (backupShared.exists()) {
                        fileCopy(backupShared, shareFile);
                    }
                    return RESTORE_OK;
                } catch (Exception e) {
                    e.printStackTrace();
                    return RESTORE_FAIL;
                }
            } else if (command.equals(COMMAND_RESET)) {
                try {
                    if (dbFile.delete()) {
                        if (shareFile.exists()) {
                            if (shareFile.delete()) {
                                return RESET_OK;
                            }
                        } else {
                            return RESET_OK;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return RESET_FAIL;
            } else {
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (dialog.isShowing()) {
                dialog.dismiss();

            }

            int resId = -1;
            switch (result.intValue()) {
            case HAVE_NO_SDCARD:
                resId = R.string.launcher_settings_have_no_sdcard;
                break;
            case BACKUP_OK:
                resId = R.string.launcher_settings_backup_sucess;
                if (mOnBackupAndRestoreListener != null) {
                    mOnBackupAndRestoreListener.onBckup(true);
                }
                break;
            case BACKUP_FAIL:
                resId = R.string.launcher_settings_backup_failed;
                if (mOnBackupAndRestoreListener != null) {
                    mOnBackupAndRestoreListener.onBckup(false);
                }
                break;
            case RESTORE_OK:
                resId = R.string.launcher_settings_restore_sucess;
                if (mOnBackupAndRestoreListener != null) {
                    mOnBackupAndRestoreListener.onRestore(true);
                }
                break;
            case RESTORE_FAIL:
                resId = R.string.launcher_settings_restore_failed;
                if (mOnBackupAndRestoreListener != null) {
                    mOnBackupAndRestoreListener.onRestore(false);
                }
                break;
            case RESET_OK:
                resId = R.string.launcher_settings_reset_sucess;
                if (mOnBackupAndRestoreListener != null) {
                    mOnBackupAndRestoreListener.onReset(true);
                }
                break;
            case RESET_FAIL:
                resId = R.string.launcher_settings_reset_failed;
                if (mOnBackupAndRestoreListener != null) {
                    mOnBackupAndRestoreListener.onReset(false);
                }
                break;

            default:
                break;
            }
            if (resId > 0) {
                Toast.makeText(mContext, mContext.getString(resId), Toast.LENGTH_SHORT).show();
            }
        }

        private void fileCopy(File dbFile, File backup) throws IOException {
            FileInputStream inputStream = new FileInputStream(dbFile);
            FileOutputStream outputStream = new FileOutputStream(backup);
            FileChannel inChannel = inputStream.getChannel();
            FileChannel outChannel = outputStream.getChannel();
            try {
                inChannel.transferTo(0, inChannel.size(), outChannel);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inChannel != null) {
                    inChannel.close();
                }
                if (outChannel != null) {
                    outChannel.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        }

        private void delDirectory(File dir) {
            if (dir != null && dir.isDirectory()) {
                for (File file : dir.listFiles()) {
                    file.delete();
                }
            }
        }
    }

    public long getLastestRestoreTime() {
        File exportDir = new File(Environment.getExternalStorageDirectory(), BACKUP_FILE);
        File backupDb = new File(exportDir, LAUNCHER_DATABASE_NAME);
        if (backupDb.exists()) {
            return backupDb.lastModified();
        }
        return -1;
    }

    interface OnBackupAndRestoreListener {
        void onBckup(boolean sucess);

        void onRestore(boolean sucess);

        void onReset(boolean sucess);
    }
}
