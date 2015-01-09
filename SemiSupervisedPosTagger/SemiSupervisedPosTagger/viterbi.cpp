//
// Created by Mohammad Sadegh Rasooli on 1/8/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//

#include "viterbi.h"
#include "iostream"

vector<int> viterbi::viterbi_third_order(sentence sentence, averaged_perceptron perceptron,const int tag_size,const int feat_size,bool is_decode) {
	int len = sentence.length + 1;

	float inf = INFINITY;

	// pai score values
	float pai[len][tag_size][tag_size];
	float emission_score[len - 1][tag_size];

	for (int position = 0; position < sentence.length; position++) {
		vector<int> emission_features = sentence.getـ_emission_features(position);
		for (int t = 2; t < tag_size; t++) {
			emission_score[position][t] = perceptron.score(emission_features, t, is_decode);
		}
	}

	// back pointer
	int bp[len][tag_size][tag_size];

	// initialization
	pai[0][0][0] = 0;
	for (int u = 2; u < tag_size; u++) {
		for (int v = 2; v < tag_size; v++) {
			pai[0][u][v] = -inf;
		}
	}

	for (int k = 1; k < len; k++) {
		for (int v = 2; v < tag_size; v++) {
			for (int u = 2; u < tag_size; u++) {
				float max_val = -inf;
				int argmax = 0;

				float bigram_score = perceptron.score(v, feat_size - 2, u, is_decode);
				for (int w = 1; w < tag_size; w++) {
					if (w == 1 && k > 1)
						continue;
					int bigram = (w << 10) + u;
					float trigram_score = perceptron.score(v, feat_size - 1, bigram, is_decode);

					float score = trigram_score + bigram_score + emission_score[k - 1][v] + pai[k - 1][w][u];
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
	for (int u = 2; u < tag_size; u++) {
		for (int v = 2; v < tag_size; v++) {
			float bigram_score = perceptron.score(1, feat_size - 2, v, is_decode);
			int bigram = (u << 10) + v;
			float trigram_score = perceptron.score(1, feat_size - 1, bigram, is_decode);
			float score = bigram_score + trigram_score + pai[len - 1][u][v];
			if (score > max_val) {
				max_val = score;
				y1 = u;
				y2 = v;
			}
		}
	}

	vector<int> tags;
	tags.insert(tags.begin(), y2);
	tags.insert(tags.begin(), y1);
	for (int k = len - 3; k >= 1; k--) {
		tags.insert(tags.begin(), bp[k+2][tags.at(0)][tags.at(1)]);
	}

	return tags;
}
