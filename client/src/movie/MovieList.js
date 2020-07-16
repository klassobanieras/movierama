import React, {Component} from 'react';
import {
    getAllMovies,
    getMoviesCreatedByUser,
    expressReaction,
    removeReaction,
    getAllMoviesOrderedByLikes,
    getAllMoviesOrderedByHates
} from '../utils/APIClient';
import Movie from './Movie';
import LoadIndicator from '../common/LoadIndicator';
import {Button, notification} from 'antd';
import {LikeOutlined, DislikeOutlined, CalendarOutlined, PlusOutlined} from '@ant-design/icons';
import {MOVIES_LIST_SIZE} from '../constants/Constants';
import {withRouter} from 'react-router-dom';
import './movieList.css';
import {AuthService} from "../utils/AuthService";

class MovieList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            movies: [],
            page: 0,
            size: 10,
            totalElements: 0,
            totalPages: 0,
            last: true,
            isLoading: false,
            order: 'date'
        };
        this.loadMovieList = this.loadMovieList.bind(this);
        this.handleLoadMore = this.handleLoadMore.bind(this);
        this.handleLikeOrdering = this.handleLikeOrdering.bind(this);
        this.handleHateOrdering = this.handleHateOrdering.bind(this);
        this.handleDateOrdering = this.handleDateOrdering.bind(this);

        this.refreshTheOrder = this.refreshTheOrder.bind(this);
    }

    loadMovieList(page = 0, size = MOVIES_LIST_SIZE) {
        let promise;
        if (this.props.username) {
            if (this.props.type === 'USER_CREATED_MOVIES') {
                promise = getMoviesCreatedByUser(this.props.username, page, size);
            }
        } else {
            promise = getAllMovies(page, size);
        }

        if (!promise) {
            return;
        }

        this.setState({
            isLoading: true
        });

        promise
            .then(response => {
                const movies = this.state.movies.slice();

                this.setState({
                    movies: (page === 0) ? response.content : movies.concat(response.content),
                    page: response.pageable.pageNumber,
                    size: response.size,
                    totalElements: response.totalElements,
                    totalPages: response.totalPages,
                    last: response.last,
                    isLoading: false,
                    order: 'date'
                })
            }).catch(error => {
            this.setState({
                isLoading: false
            })
        });

    }

    componentDidMount() {
        this.loadMovieList();
    }

    componentDidUpdate(nextProps) {
        if (this.props.isAuthenticated !== nextProps.isAuthenticated) {
            // Reset State
            this.setState({
                movies: [],
                page: 0,
                size: 10,
                totalElements: 0,
                totalPages: 0,
                last: true,
                isLoading: false,
                order: 'date'
            });
            this.loadMovieList();
        }
    }

    handleLoadMore() {

        if (this.state.order === 'date') {
            this.loadMovieList(this.state.page + 1);
        } else if (this.state.order === 'like') {
            this.loadLikeOrdering(this.state.page + 1);
        } else if (this.state.order === 'hate') {
            this.loadHateOrdering(this.state.page + 1);
        }
    }

    refreshTheOrder() {
        if (this.state.order === 'date') {
            this.loadMovieList();
        } else if (this.state.order === 'like') {
            this.loadLikeOrdering();
        } else if (this.state.order === 'hate') {
            this.loadHateOrdering();
        }
    }

    handleLike(event, movieIndex) {
        this.handleReactionSubmit(event, movieIndex, 'like');
    }

    handleHate(event, movieIndex) {
        this.handleReactionSubmit(event, movieIndex, 'hate');
    }

    handleReactionSubmit(event, movieIndex, selectedChoice) {
        event.preventDefault();

        if (!this.props.isAuthenticated) {
            this.props.history.push("/login");
            notification.info({
                message: 'Movierama',
                description: "Please login to put an opinion.",
            });
            return;
        }

        const movie = this.state.movies[movieIndex];

        console.log(selectedChoice);
        const reactionData = {
            movieId: movie.movieId,
            reaction: selectedChoice
        };

        expressReaction(reactionData)
            .then(() => {
                const movies = this.state.movies.slice();
                selectedChoice === 'like'
                    ? movies[movieIndex].countOfLikes += 1
                    : movies[movieIndex].countOfHates += 1;
                movies[movieIndex].currentUserReaction = selectedChoice.toUpperCase();

                this.setState({
                    movies: movies,
                });

            }).catch(error => {
            if (error.status === 401) {
                AuthService.signOut();
            } else {
                notification.error({
                    message: 'Movierama',
                    description: error.message || 'Sorry! Something went wrong. Please try again!'
                });
            }
        });
    }

    handleClearVote(event, movieIndex) {
        event.preventDefault();
        if (!this.props.isAuthenticated) {
            this.props.history.push("/login");
            notification.info({
                message: 'Movierama',
                description: "Please login to put an opinion.",
            });
            return;
        }

        const movie = this.state.movies[movieIndex];

        removeReaction(movie.movieId)
            .then(() => {
                const movies = this.state.movies.slice();
                movies[movieIndex].currentUserReaction === 'LIKE'
                    ? movies[movieIndex].countOfLikes -= 1
                    : movies[movieIndex].countOfHates -= 1;
                movies[movieIndex].currentUserReaction = 'NONE';

                this.setState({
                    movies: movies,
                });
            }).catch(error => {
            if (error.status === 401) {
                AuthService.signOut();
            } else {
                notification.error({
                    message: 'Movierama',
                    description: error.message || 'Sorry! Something went wrong. Please try again!'
                });
            }
        });
    }

    handleLikeOrdering() {
        this.loadLikeOrdering();
    }

    loadLikeOrdering(page = 0, size = MOVIES_LIST_SIZE) {
        let promise;
        promise = getAllMoviesOrderedByLikes(page, size);

        if (!promise) {
            return;
        }

        this.setState({
            isLoading: true
        });

        promise
            .then(response => {
                const movies = this.state.movies.slice();

                this.setState({
                    movies: (page === 0) ? response.content : movies.concat(response.content),
                    page: response.page,
                    size: response.size,
                    totalElements: response.totalElements,
                    totalPages: response.totalPages,
                    last: response.last,
                    isLoading: false,
                    order: 'like'
                })
            }).catch(error => {
            this.setState({
                isLoading: false
            })
        });
    }

    handleHateOrdering() {
        this.loadHateOrdering();
    }

    loadHateOrdering(page = 0, size = MOVIES_LIST_SIZE) {
        let promise = getAllMoviesOrderedByHates(page, size);

        if (!promise) {
            return;
        }

        this.setState({
            isLoading: true
        });

        promise
            .then(response => {
                const movies = this.state.movies.slice();

                this.setState({
                    movies: (page === 0) ? response.content : movies.concat(response.content),
                    page: response.page,
                    size: response.size,
                    totalElements: response.totalElements,
                    totalPages: response.totalPages,
                    last: response.last,
                    isLoading: false,
                    order: 'hate'
                })
            }).catch(error => {
            this.setState({
                isLoading: false
            })
        });
    }

    handleDateOrdering() {
        this.loadMovieList();
    }

    render() {
        const movieViews = [];
        this.state.movies.forEach((movie, movieId) => {
            movieViews.push(<Movie
                key={movie.movieId}
                movie={movie}
                currentVote={movie.currentUserReaction}
                currentUsername={this.props.currentUser ? this.props.currentUser : null}
                handleClearVote={(event) => this.handleClearVote(event, movieId)}
                handleLike={(event) => this.handleLike(event, movieId)}
                handleHate={(event) => this.handleHate(event, movieId)}/>)
        });

        const orderingChoices = [];
        if (!this.props.isProfileMovieList) {
            let likeOrderButton = <Button key={"likeOrderButtonKey"} onClick={this.handleLikeOrdering} disabled={this.state.isLoading}>
                Order by likes<LikeOutlined/>
            </Button>
            let hateOrderButton = <Button key={"hateOrderButtonKey"} onClick={this.handleHateOrdering} disabled={this.state.isLoading}>
                Order by hates<DislikeOutlined/>
            </Button>
            let dateOrderButton = <Button key={"dateOrderButtonKey"} onClick={this.handleDateOrdering} disabled={this.state.isLoading}>
                Order by Date<CalendarOutlined/>
            </Button>
            orderingChoices.push(likeOrderButton);
            orderingChoices.push(hateOrderButton);
            orderingChoices.push(dateOrderButton)

        }
        return (
            <div className="movies-container">

                {orderingChoices}
                {movieViews}
                {
                    !this.state.isLoading && this.state.movies.length === 0 ? (
                        <div className="no-movies-found">
                            <span>No Movies Found.</span>
                        </div>
                    ) : null
                }
                {
                    !this.state.isLoading && !this.state.last ? (
                        <div className="load-more-movies">
                            <Button type="dashed" onClick={this.handleLoadMore} disabled={this.state.isLoading}>
                                <PlusOutlined/> Load more
                            </Button>
                        </div>) : null
                }
                {
                    this.state.isLoading ?
                        <LoadIndicator/> : null
                }
            </div>
        );
    }
}

export default withRouter(MovieList);