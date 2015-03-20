package SemiSupervisedPOSTagger.Structures;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 1/8/15
 * Time: 4:50 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */

public enum SpecialWords {
    start(0),
    stop(1),
    unknown(-1);

    public int value;

    SpecialWords(int value) {
        this.value=value;
    }
}
