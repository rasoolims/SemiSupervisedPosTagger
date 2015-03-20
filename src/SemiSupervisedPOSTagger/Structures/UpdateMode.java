package SemiSupervisedPOSTagger.Structures;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 1/14/15
 * Time: 11:21 AM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */

public enum UpdateMode {
    standard(0),
    early(1),
    maxViolation(2);

    public int value;

    UpdateMode(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if (value == 0)
            return "standard update";
        if (value == 1)
            return "early update";
        return "max violation update";
    }
}
