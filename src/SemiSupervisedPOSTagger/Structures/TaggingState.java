package SemiSupervisedPOSTagger.Structures;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 1/13/15
 * Time: 7:12 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */

public class TaggingState implements Cloneable{
    public int[] tags;
    public int currentPosition;
    public float score;

    public TaggingState(int[] tags, int currentPosition,float score) {
        this.tags = tags;
        this.currentPosition = currentPosition;
        this.score=score;
    }

    public TaggingState( int size) {
        tags=new int[size];
        this.currentPosition = 0;
        score=0;
    }

    @Override
    public boolean equals(Object o){
        return false;
    }

    @Override
    public TaggingState clone(){
       int[] t=new int[tags.length];
        for(int i=0;i<=currentPosition;i++){
            t[i]=tags[i];
        }
        return new TaggingState(t,currentPosition,score);
    }
}
