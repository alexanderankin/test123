/*
 * WincryptCipherPlugin - A jEdit plugin as wincrypt cipher implementation for the CipherPlugin
 * :tabSize=4:indentSize=4:noTabs=true:
 *
 * Copyright (C) 2007 Björn "Vampire" Kautler
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

#include "WincryptCipher.h"
#include <wtypes.h>
#include <wincrypt.h>

static jboolean get_crypto_dll(HINSTANCE* pdll) {
    HINSTANCE dll = LoadLibraryA("Crypt32.dll");
    if (dll) {
        if (NULL != pdll) {
            *pdll = dll;
        } else {
            FreeLibrary(dll);
        }
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

static jboolean get_crypto_function(const char* name, HINSTANCE* pdll, FARPROC* pfn) {
    HINSTANCE dll;
    if (get_crypto_dll(&dll)) {
        FARPROC fn = GetProcAddress(dll,name);
        if (fn) {
            *pdll = dll;
            *pfn = fn;
            return JNI_TRUE;
        }
        FreeLibrary(dll);
    }
    return JNI_FALSE;
}

JNIEXPORT jbyteArray JNICALL Java_wincrypt_WincryptCipher_encryptNative(JNIEnv * env,
                                                                        jobject jObject,
                                                                        jbyteArray rawDataArray,
                                                                        jcharArray descriptionArray,
                                                                        jbyteArray entropyArray) {
    typedef BOOL (CALLBACK *encrypt_fn)
        (DATA_BLOB *,                 /* pDataIn */
         LPCWSTR,                     /* szDataDescr */
         DATA_BLOB *,                 /* pOptionalEntropy */
         PVOID,                       /* pvReserved */
         CRYPTPROTECT_PROMPTSTRUCT *, /* pPromptStruct */
         DWORD,                       /* dwFlags */
         DATA_BLOB *);                /* pDataOut */
    
    HINSTANCE dll;
    FARPROC fn;
    encrypt_fn encrypt;
    jbyte* rawData;
    DATA_BLOB blobin;
    jchar* description;
    size_t length;
    jchar* copiedDescription;
    jbyte* entropy;
    DATA_BLOB entropyParam;
    DATA_BLOB blobout;
    BOOL crypted;
    jbyteArray result;                                                                                       //FILE* logfile;
    
    if (!rawDataArray) {
        jthrowable npe;
        npe = (*env)->FindClass(env,"java/lang/NullPointerException");
        if (npe) {
            (*env)->ThrowNew(env,npe,"rawData must not be null");
        }
        return NULL;
    }
//#define printf(string) logfile = fopen("C:\\Programme\\jEdit\\logfile.txt","a"); fprintf(logfile,string); fprintf(logfile,"\n"); fflush(logfile); fclose(logfile)
                                                                                                             //printf("rawDataArray != null");
    
    if (!descriptionArray) {
        jthrowable npe;
        npe = (*env)->FindClass(env,"java/lang/NullPointerException");
        if (npe) {
            (*env)->ThrowNew(env,npe,"description must not be null");
        }
        return NULL;
    }                                                                                                        //printf("descriptionArray != null");
    
    if (!entropyArray) {
        jthrowable npe;
        npe = (*env)->FindClass(env,"java/lang/NullPointerException");
        if (npe) {
            (*env)->ThrowNew(env,npe,"entropy must not be null");
        }
        return NULL;
    }                                                                                                        //printf("entropyArray != null");
    
    if (!get_crypto_function("CryptProtectData",&dll,&fn)) {
        return NULL;
    }                                                                                                        //printf("crypto_function == TRUE");
    encrypt = (encrypt_fn)fn;                                                                                //printf("encrypt = (encrypt_fn)fn");
    rawData = (*env)->GetByteArrayElements(env,rawDataArray,NULL);                                           //printf("rawData = ");
    blobin.cbData = (*env)->GetArrayLength(env,rawDataArray);                                                //printf("blobin.cbData = ");
    blobin.pbData = rawData;                                                                                 //printf("blobin.pbData = ");
    description = (*env)->GetCharArrayElements(env,descriptionArray,NULL);                                   //printf("description = ");
    length = (*env)->GetArrayLength(env,descriptionArray);                                                   //printf("length = ");
    copiedDescription = (jchar*)calloc(length+1,sizeof(jchar));                                              //printf("copiedDescription = ");
    if (!copiedDescription) {
        jthrowable oome;
        oome = (*env)->FindClass(env,"java/lang/OutOfMemoryError");
        if (oome) {
            (*env)->ThrowNew(env,oome,"it seems you are running out of memory");
        }
        FreeLibrary(dll);
        return NULL;
    }                                                                                                        //printf("copiedDescription != NULL");
    lstrcpynW(copiedDescription,description,length+1);                                                       //printf("lstrcpynW(copiedDescription,description,length+1)");
    (*env)->ReleaseCharArrayElements(env,descriptionArray,description,0);                                    //printf("ReleaseCharArrayElements(env,descriptionArray,description,0)");
    entropy = (*env)->GetByteArrayElements(env,entropyArray,NULL);                                           //printf("entropy = ");
    entropyParam.cbData = (*env)->GetArrayLength(env,entropyArray);                                          //printf("entropyParam.cbData = ");
    entropyParam.pbData = entropy;                                                                           //printf("entropyParam.pbData = ");
    crypted = encrypt(&blobin,copiedDescription,&entropyParam,NULL,NULL,CRYPTPROTECT_UI_FORBIDDEN,&blobout); //printf("crypted = encrypt()");
    free(copiedDescription);                                                                                 //printf("free(copiedDescription)");
    (*env)->ReleaseByteArrayElements(env,rawDataArray,rawData,0);                                            //printf("ReleaseByteArrayElements(env,rawDataArray,rawData,0)");
    (*env)->ReleaseByteArrayElements(env,entropyArray,entropy,0);                                            //printf("ReleaseByteArrayElements(env,entropyArray,entropy,0)");
    FreeLibrary(dll);                                                                                        //printf("FreeLibrary(dll)");
    if (crypted) {                                                                                           //printf("crypted == TRUE");
        result = (*env)->NewByteArray(env,blobout.cbData);                                                   //printf("result = (*env)->NewByteArray(env,blobout.cbData)");
        (*env)->SetByteArrayRegion(env,result,0,blobout.cbData,blobout.pbData);                              //printf("SetByteArrayRegion(env,result,0,blobout.cbData,blobout.pbData)");
        LocalFree(blobout.pbData);                                                                           //printf("LocalFree(blobout.pbData)");
    } else {                                                                                                 //printf("crypted == FALSE");
        result = NULL;                                                                                       //printf("result = NULL");
    }                                                                                                        //printf("return");
    return result;
}

JNIEXPORT jbyteArray JNICALL Java_wincrypt_WincryptCipher_decryptNative(JNIEnv * env,
                                                                        jobject jObject,
                                                                        jbyteArray encyrptedDataArray,
                                                                        jcharArray descriptionArray,
                                                                        jbyteArray entropyArray) {
    typedef BOOL (CALLBACK *decrypt_fn)
        (DATA_BLOB *,                 /* pDataIn */
         LPCWSTR *,                   /* szDataDescr */
         DATA_BLOB *,                 /* pOptionalEntropy */
         PVOID,                       /* pvReserved */
         CRYPTPROTECT_PROMPTSTRUCT *, /* pPromptStruct */
         DWORD,                       /* dwFlags */
         DATA_BLOB *);                /* pDataOut */
    
    HINSTANCE dll;
    FARPROC fn;
    decrypt_fn decrypt;
    jbyte* encyrptedData;
    DATA_BLOB blobin;
    jchar* decryptedDescription;
    jbyte* entropy;
    DATA_BLOB entropyParam;
    DATA_BLOB blobout;
    BOOL decrypted;
    jchar* description;
    size_t length;
    jchar* copiedDescription;
    jbyteArray result;                                                                                             //FILE* logfile; char* temp; const char temp2[50]; char* temp3;
    
    if (!encyrptedDataArray) {
        jthrowable npe;
        npe = (*env)->FindClass(env,"java/lang/NullPointerException");
        if (npe) {
            (*env)->ThrowNew(env,npe,"encyrptedData must not be null");
        }
        return NULL;
    }                                                                                                              //printf("encyrptedDataArray != null");
    
    if (!descriptionArray) {
        jthrowable npe;
        npe = (*env)->FindClass(env,"java/lang/NullPointerException");
        if (npe) {
            (*env)->ThrowNew(env,npe,"description must not be null");
        }
        return NULL;
    }                                                                                                              //printf("descriptionArray != null");
    
    if (!entropyArray) {
        jthrowable npe;
        npe = (*env)->FindClass(env,"java/lang/NullPointerException");
        if (npe) {
            (*env)->ThrowNew(env,npe,"entropy must not be null");
        }
        return NULL;
    }                                                                                                              //printf("entropyArray != null");
    
    if (!get_crypto_function("CryptUnprotectData",&dll,&fn)) {
        return NULL;
    }                                                                                                              //printf("crypto_function == TRUE");
    decrypt = (decrypt_fn)fn;                                                                                      //printf("decrypt = (decrypt_fn)fn");
    encyrptedData = (*env)->GetByteArrayElements(env,encyrptedDataArray,NULL);                                     //printf("encyrptedData = ");
    blobin.cbData = (*env)->GetArrayLength(env,encyrptedDataArray);                                                //printf("blobin.cbData = ");
    blobin.pbData = encyrptedData;                                                                                 //printf("blobin.pbData = ");
    entropy = (*env)->GetByteArrayElements(env,entropyArray,NULL);                                                 //printf("entropy = ");
    entropyParam.cbData = (*env)->GetArrayLength(env,entropyArray);                                                //printf("entropyParam.cbData = ");
    entropyParam.pbData = entropy;                                                                                 //printf("entropyParam.pbData = ");
    decrypted = decrypt(&blobin,&decryptedDescription,&entropyParam,NULL,NULL,CRYPTPROTECT_UI_FORBIDDEN,&blobout); //printf("decrypted = decrypt()");
    (*env)->ReleaseByteArrayElements(env,encyrptedDataArray,encyrptedData,0);                                      //printf("ReleaseByteArrayElements(env,encyrptedDataArray,encyrptedData,0)");
    (*env)->ReleaseByteArrayElements(env,entropyArray,entropy,0);                                                  //printf("ReleaseByteArrayElements(env,entropyArray,entropy,0)");
    FreeLibrary(dll);                                                                                              //printf("FreeLibrary(dll)");
    if (decrypted) {                                                                                               //printf("decrypted == TRUE");
        description = (*env)->GetCharArrayElements(env,descriptionArray,NULL);                                     //printf("description = ");
        length = (*env)->GetArrayLength(env,descriptionArray);                                                     //printf("length = ");
        copiedDescription = (jchar*)calloc(length+1,sizeof(jchar));                                                //printf("copiedDescription = ");
        if (!copiedDescription) {
            jthrowable oome;
            oome = (*env)->FindClass(env,"java/lang/OutOfMemoryError");
            if (oome) {
                (*env)->ThrowNew(env,oome,"it seems you are running out of memory");
            }
            free(copiedDescription);
            LocalFree(blobout.pbData);
            return NULL;
        }                                                                                                          //printf("copiedDescription != NULL");
        lstrcpynW(copiedDescription,description,length+1);                                                         //printf("lstrcpynW(copiedDescription,description,length+1)");
        (*env)->ReleaseCharArrayElements(env,descriptionArray,description,0);                                      //printf("ReleaseCharArrayElements(env,descriptionArray,description,0)");
        if (0 == lstrcmpW(copiedDescription,decryptedDescription)) {                                               //printf("copiedDescription == decryptedDescription");
            result = (*env)->NewByteArray(env,blobout.cbData);                                                     //printf("result = (*env)->NewByteArray(env,blobout.cbData)");
            (*env)->SetByteArrayRegion(env,result,0,blobout.cbData,blobout.pbData);                                //printf("SetByteArrayRegion(env,result,0,blobout.cbData,blobout.pbData)");
        } else {                                                                                                   //printf("copiedDescription != decryptedDescription");
                                                                                                                   //temp = (char*)description;
                                                                                                                   //temp3 = (char*)temp2;
                                                                                                                   //while (*temp) {
                                                                                                                   //    *temp3 = *temp;
                                                                                                                   //    temp += 2;
                                                                                                                   //    temp3++;
                                                                                                                   //}
                                                                                                                   //*temp3 = 0;
                                                                                                                   //printf(temp2);
                                                                                                                   //temp = (char*)decryptedDescription;
                                                                                                                   //temp3 = (char*)temp2;
                                                                                                                   //while (*temp) {
                                                                                                                   //    *temp3 = *temp;
                                                                                                                   //    temp += 2;
                                                                                                                   //    temp3++;
                                                                                                                   //}
                                                                                                                   //*temp3 = 0;
                                                                                                                   //printf(temp2);
                                                                                                                   //#undef printf
                                                                                                                   //logfile = fopen("C:\\Programme\\jEdit\\logfile.txt","a");
                                                                                                                   //fprintf(logfile,"ArrayLength: %d\n",(*env)->GetArrayLength(env,descriptionArray));
                                                                                                                   //temp = (char*)copiedDescription;
                                                                                                                   //while (*temp || *(temp+1)) {
                                                                                                                   //    fprintf(logfile,"%d, %d, ",(int)*temp,(int)*(temp+1));
                                                                                                                   //    temp += 2;
                                                                                                                   //}
                                                                                                                   //fprintf(logfile,"\n");
                                                                                                                   //temp = (char*)decryptedDescription;
                                                                                                                   //while (*temp || *(temp+1)) {
                                                                                                                   //    fprintf(logfile,"%d, %d, ",(int)*temp,(int)*(temp+1));
                                                                                                                   //    temp += 2;
                                                                                                                   //}
                                                                                                                   //fprintf(logfile,"\n");
                                                                                                                   //fflush(logfile);
                                                                                                                   //fclose(logfile);
            result = NULL;                                                                                         //printf("result = NULL");
        }
        free(copiedDescription);                                                                                   //printf("free(copiedDescription)");
        LocalFree(blobout.pbData);                                                                                 //printf("LocalFree(blobout.pbData)");
    } else {                                                                                                       //printf("decrypted == FALSE");
        result = NULL;                                                                                             //printf("result = NULL");
    }                                                                                                              //printf("return");
    return result;
}

JNIEXPORT jboolean JNICALL Java_wincrypt_WincryptCipher_isNativeAvailable(JNIEnv * env,
                                                                          jobject jObject) {
    return get_crypto_dll(NULL);
}
