package SemiSupervisedPOSTagger.Structures;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 1/13/15
 * Time: 7:05 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */

public class BeamElement implements Comparable<BeamElement> {
    public int tagDecision;
    public float score;
    public int beamNum;

    public BeamElement(int tagDecision, float score, int beamNum) {
        this.tagDecision = tagDecision;
        this.score = score;
        this.beamNum = beamNum;
    }

    @Override
    public int compareTo(BeamElement beamElement) {
        float diff=score-beamElement.score;
        if(diff>0)
            return 1;
        else if(diff<0)
            return -1;
        else{
            diff=beamNum-beamElement.beamNum;
            if(diff>0)
                return 1;
            else if(diff<0)
                return -1;
            else{
                return (tagDecision-beamElement.tagDecision);
            }
        }
    }

    @Override
    public boolean equals(Object o){
        return false;
    }
}
