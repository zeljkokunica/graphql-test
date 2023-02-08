package com.viabill.graphqltest.criipto.configuration;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseValueException;
import graphql.schema.GraphQLScalarType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;

public final class CriiptoGraphQLScalars {

	public static final GraphQLScalarType BLOB = GraphQLScalarType.newScalar()
			.name("blob")
			.description("A custom scalar to handle blobs")
			.coercing(new Coercing() {
				@Override
				public Object serialize(Object dataFetcherResult) {
					return Optional.ofNullable(dataFetcherResult).map(Object::toString).orElse(null);
				}

				@Override
				public Object parseValue(Object input) {
					return Objects.requireNonNull(Optional.ofNullable(input).map(Object::toString).orElse(null));
				}

				@Override
				public Object parseLiteral(Object input) {
					if (input instanceof StringValue) {
						return ((StringValue) input).getValue();
					}
					return Objects.requireNonNull(Optional.ofNullable(input).map(Object::toString).orElse(null));
				}
			})
			.build();

	public static final GraphQLScalarType URI = GraphQLScalarType.newScalar()
			.name("uri")
			.description("A custom scalar to handle URIs")
			.coercing(new Coercing() {
				@Override
				public Object serialize(Object dataFetcherResult) {
					return Optional.ofNullable(dataFetcherResult).map(Object::toString).orElse(null);
				}

				@Override
				public Object parseValue(Object input) {

					return Objects.requireNonNull(Optional.ofNullable(input).map(Object::toString).map(s -> {
						try {
							return new URI(s);
						} catch (URISyntaxException e) {
							throw new CoercingParseValueException(e);
						}
					}).orElse(null));
				}

				@Override
				public Object parseLiteral(Object input) {
					return Objects.requireNonNull(Optional.ofNullable(input).map(Object::toString).orElse(null));
				}
			})
			.build();

}
