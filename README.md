# Story Point Predictor  

## Overview  
This project uses machine learning to **predict Agile story points** from user stories (e.g., Jira issues, GitHub issues). By training on public datasets of labeled stories, the model learns to approximate how teams assign effort values to stories.  
The main goal is to use LLMs to augment results obtained from an a conventional regression model.

The goal is not to replace human judgment, but to **provide guidance** and **calibration** for estimation, especially when teams want to reduce bias or speed up sprint planning.  

---

## Features  
- **Text Embedding Representation**: Convert story descriptions and acceptance criteria into vector embeddings using pretrained language models.  
- **Neural Network Regression**: Train a regression model that predicts story points from embeddings.  
- **Cross-Validation**: Apply k-fold cross-validation to reduce overfitting and measure generalization performance.  
- **Distribution Output**: For any given story, produce a distribution graph of viable story point estimates (not just a single number).  
- **Fine-Tuning Option**: Adapt the model on a small set of team-specific data to capture a particular team’s story-pointing culture.  

---

## Why This Matters  
- Story points are subjective and vary across teams.  
- Historical datasets are small and noisy.  
- This project explores whether embedding-based ML models can capture **relative complexity trends** and provide useful input into estimation conversations.  

## Running and Building
This project is using uv as the virtual environment manager.
source .venv/bin/activate 
pre-commit install
pre-commit run --all-files

## References
For raw data
https://solar.cs.ucl.ac.uk/pdf/tawosi2022msr.pdf
https://github.com/morakotch/datasets/tree/master

This paper suggests that current methods that rely solely on story descriptions provide an inaccurate baseline.
https://arxiv.org/pdf/2201.05401
Main reason that this repo strives to provide meeting augmented AI prediction. A lot of context is needed for proper story pointing and estimation. 
Normally, story descriptions are not all encompassing of the potential blockers, dependencies or pre-existing work.

This paper suggests an ML model to estimate story points in agile software development.
Instead of treating story point prediction as a pure regression problem, they used a comparative learning approach.
They compared pairs of issues to learn relative complexity.
Then, they used those comparisons to better predict story points.
The main idea was to overcome the noisy and subjective nature of raw story point labels by leveraging pairwise judgments.
https://arxiv.org/pdf/2507.14642
My per-team fine-tuning path removes the main operational "tax" of the comparative-learning paper (no pairwise data, simpler training/inference), while directly tackling cross-team scale drift. 

This paper suggests a post-hoc corrector to propose corrections for the predictions of an arbitrary ML model.
https://arxiv.org/pdf/2402.13414
