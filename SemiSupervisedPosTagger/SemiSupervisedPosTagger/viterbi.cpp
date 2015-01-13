//
// Created by Mohammad Sadegh Rasooli on 1/8/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//

#include "viterbi.h"
#include "iostream"

vector<int> viterbi::viterbi_third_order(sentence  sentence, averaged_perceptron  perceptron,bool is_decode) {
	int len = sentence.length + 1;

	float inf = 1000000;

	int tag_size=perceptron.tag_size;
	int feat_size=perceptron.feat_size;
	// pai score values
	float pai[len][tag_size][tag_size];
	float emission_score[len - 1][tag_size];
	float bigram_score[tag_size][tag_size];
	float trigram_score[tag_size][tag_size][tag_size];

	for (int v = 0; v < tag_size; v++) {
		for (int u = 0; u < tag_size; u++) {
			bigram_score[u][v] = perceptron.score(v, feat_size - 2, u, is_decode);
			for(int w = 0; w < tag_size; w++) {
				int bigram = (w << 10) + u;
				trigram_score[w][u][v] = perceptron.score(v, feat_size - 1, bigram, is_decode);
			}
		}
	}

	for (int position = 0; position < sentence.length; position++) {
		vector<int> emission_features = sentence.get_emission_features(position);
		for (int t = 2; t < tag_size; t++) {
			emission_score[position][t] = perceptron.score(emission_features, t, is_decode);
		}
	}

	// back pointer
	int bp[len][tag_size][tag_size];

	// initialization
	pai[0][0][0] = 0;
	for (int u = 1; u < tag_size; u++) {
		for (int v = 1; v < tag_size; v++) {
			pai[0][u][v] = -inf;
		}
	}

	for (int k = 1; k < len; k++) {
		for (int v = 2; v < tag_size; v++) {
			for (int u = 0; u < tag_size; u++) {
			    if(u==1)
				    continue;
				float max_val = -inf;
				int argmax = 0;


				for (int w = 0; w < tag_size; w++) {
					if (w == 1 || (w==0 && k>1) || (k==1 && w!=0))
						continue;
					float score = trigram_score[w][u][v] + bigram_score[u][v] + emission_score[k - 1][v] + pai[k - 1][w][u];
					if (score > max_val) {
						max_val = score;
						argmax = w;
					}
				}
				pai[k][u][v] = max_val;
				bp[k][u][v] = argmax;
			}
		}
	}

	int y1 = 0;
	int y2 = 0;
	float max_val = -inf;
	if(sentence.length>1) {
		for (int u = 2; u < tag_size; u++) {
			for (int v = 2; v < tag_size; v++) {
				float score = bigram_score[v][1] + trigram_score[u][v][1] + pai[len - 1][u][v];
				if (score > max_val) {
					max_val = score;
					y1 = u;
					y2 = v;
				}
			}
		}
	}else{
		for (int v = 2; v < tag_size; v++) {
			float score = bigram_score[v][1] + trigram_score[0][v][1] + pai[len - 1][0][v];
			if (score > max_val) {
				max_val = score;
				y2 = v;
			}
		}
	}

	vector<int> tags;
	tags.insert(tags.begin(), y2);
	if(sentence.length>1)
		tags.insert(tags.begin(), y1);
	for (int k = len - 3; k >= 1; k--) {
		tags.insert(tags.begin(), bp[k+2][tags.at(0)][tags.at(1)]);
	}

	return tags;
}
