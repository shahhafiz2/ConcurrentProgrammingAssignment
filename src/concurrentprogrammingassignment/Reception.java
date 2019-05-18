package concurrentprogrammingassignment;
// @author Shah

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Reception {
    Doctor doctors[];
    CommonWaitingList cwl;
    ReentrantLock lock = new ReentrantLock();
    Condition ListNotEmpty = lock.newCondition();
    Condition FinishConsultation = lock.newCondition();

    public Reception(Doctor d[], CommonWaitingList cwl) {
        this.doctors = d;
        this.cwl = cwl;
    }

    synchronized public void AssignPatientToDoctor(Patient patient) {
        try {
            Doctor docWithLeastPatient = findDoctorWithLeastPatient();
            if (docWithLeastPatient.waitingList.getSize() == 3) {
                cwl.addPatient(patient);
                System.out.println("All doctors are occupied. Adding " + patient.name + " to common waiting list");
            } else {
                System.out.println(patient.name+" arrived, assigned to "+docWithLeastPatient.name +" - "+System.currentTimeMillis());
//                System.out.println(patient.name+" arrived, assigned to "+docWithLeastPatient.name);
                docWithLeastPatient.addPatient(patient);
                ListNotEmptySignall();
            }
        } catch (Exception e) {
        } finally { 
        }
    }
    
    public void ListNotEmptySignall(){
        try {
            lock.lock();
            ListNotEmpty.signalAll();
        } catch (Exception e) {
        } finally { lock.unlock();}
    }

    public Doctor findDoctorWithLeastPatient() {
        Doctor d;
        d = doctors[0];
        for (Doctor doctor : doctors) {
            if (doctor.apl.getSize() < d.apl.getSize()) {
                d = doctor;
            }
        }
        return d;
    }

    boolean stopWorking = false;
    public void startConsultation(Doctor d){
        while(!stopWorking){
            while(d.waitingList.getSize() == 0){
                System.out.println(d.name+": No patient in waiting list");
                ListNotEmptyAwait();
            }
            Patient patient = d.selectPatient();
            System.out.println(d.name + " meeting " + patient.name +" - "+System.currentTimeMillis());
//                System.out.println(d.name + " meeting " + patient.name);
            waittt(patient.consultationTime);
            System.out.println(d.name+" and "+patient.name+" session has ended - "+System.currentTimeMillis());
            //                System.out.println(d.name+" and "+patient.name+" session has ended");
            d.waitingList.removePatient(patient);
        }
    }
    
    
    public void ListNotEmptyAwait(){
        try {
            lock.lock();
            ListNotEmpty.await();
        } catch (InterruptedException e) {
        } finally { lock.unlock();}
    }
    
    synchronized public void waittt(int t){
        try {
            wait(t);
        } catch (InterruptedException ex) {
            Logger.getLogger(Reception.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
}

